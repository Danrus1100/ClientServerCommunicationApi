package com.danrus.csc;

import com.danrus.csc.api.CscApi;
import com.danrus.csc.api.packet.CscPacket;
import com.danrus.csc.api.packet.CscPacketType;
import com.danrus.csc.api.packet.CscPackets;
import com.danrus.csc.api.server.CscServerService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CscPaperService implements CscServerService<Player> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CscPaperService.class);
    private final Plugin plugin;
    private final Messenger messenger;

    private final HashMap<CscPacketType<?>, List<CscServerService.ServerPacketListener<Player>>> listHashMap = new HashMap<>();
    private final HashMap<CscPacketType<?>, List<CscServerService.ServerSinglePacketListener<Player, ?>>> typedListeners = new HashMap<>();

    public CscPaperService(Plugin plugin) {
        this.messenger = plugin.getServer().getMessenger();
        this.plugin = plugin;

        messenger.registerOutgoingPluginChannel(plugin, CscApi.CHANNEL);
        messenger.registerIncomingPluginChannel(plugin, CscApi.CHANNEL, (channel, player, message) -> {
            ByteBuf buf = Unpooled.wrappedBuffer(message);
            short id = buf.readShort();

            CscPacketType<?> packetType = CscPackets.getByNumId(id);
            if (packetType == null) {
                LOGGER.warn("Received unknown packet with id: {}", id);
                return;
            }

            CscPacket packet = packetType.factory().create(buf);

            List<ServerPacketListener<Player>> listeners = listHashMap.get(packetType);
            if (listeners != null) {
                for (ServerPacketListener<Player> listener : listeners) {
                    try {
                        listener.receive(packet, player);
                    } catch (Exception e) {
                        LOGGER.error("Error while handling packet {} with listener {}", packetType.id(), listener.getClass().getName(), e);
                    }
                }
            }

            List<ServerSinglePacketListener<Player, ?>> typedList = typedListeners.get(packetType);
            if (typedList != null) {
                for (ServerSinglePacketListener<Player, ?> listener : typedList) {
                    try {
                        invokeListener(listener, packet, player);
                    } catch (Exception e) {
                        LOGGER.error("Error while handling packet {} with listener {}", packetType.id(), listener.getClass().getName(), e);
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T extends CscPacket> void invokeListener(ServerSinglePacketListener<Player, T> listener, CscPacket packet, Player player) {
        listener.receive((T) packet, player);
    }


    @Override
    public void sendPacket(@Nullable CscPacket packet, Player player) {
        if (!player.isOnline()) {
            LOGGER.warn("Tried to send packet to player {}, but they are not online", player.getName());
            return;
        }
        if (packet == null) {
            return;
        }
        ByteBuf buf = Unpooled.buffer();
        try {
            short id = CscPackets.getByClass(packet.getClass()).id();
            buf.writeShort(id);
            packet.write(buf);
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            player.sendPluginMessage(plugin, CscApi.CHANNEL, bytes);
        } finally {
            buf.release();
        }
    }

    @Override
    public void sendPacket(CscPacket packet, UUID player) {
        Player p = plugin.getServer().getPlayer(player);
        if (p != null) {
            sendPacket(packet, p);
        } else {
            LOGGER.warn("Tried to send packet to player with UUID {}, but they are not online", player);
        }
    }

    @Override
    public void broadcastPacket(CscPacket packet) {
        plugin.getServer().getOnlinePlayers().forEach(p -> sendPacket(packet, p));
    }

    @Override
    public void broadcastPacket(PacketBroadcaster<Player> broadcaster) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(broadcaster.send(player), player);
        }
    }

    @Override
    public void registerListener(ServerPacketListener<Player> listener, CscPacketType<?>... types) {
        for (CscPacketType<?> type : types) {
            listHashMap.computeIfAbsent(type, k -> new java.util.ArrayList<>()).add(listener);
        }
    }

    @Override
    public <T extends CscPacket> void registerListener(ServerSinglePacketListener<Player, T> listener, CscPacketType<T> type) {
        typedListeners.computeIfAbsent(type, k -> new java.util.ArrayList<>()).add(listener);
    }
}
