package com.danrus.csc;

import com.danrus.csc.api.CscApi;
import com.danrus.csc.api.client.CscClientService;
import com.danrus.csc.api.packet.CscPacket;
import com.danrus.csc.api.packet.CscPacketFactory;
import com.danrus.csc.api.packet.CscPacketType;
import com.danrus.csc.api.packet.CscPackets;
import com.danrus.csc.api.packet.UnknownPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.SkipPacketDecoderException;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FabricClientService implements CscClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FabricClientService.class);
    private static final CustomPacketPayload.Type<CscPacketWrapper> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.parse(CscApi.CHANNEL));
    private static final StreamCodec<RegistryFriendlyByteBuf, CscPacketWrapper> CODEC = StreamCodec.of(
            (buf, payload) -> {
                CscPacketType<?> packetType = CscPackets.getByClass(payload.packet.getClass());
                buf.writeShort(packetType.id());
                payload.packet.write(buf);
            },
            buf -> {
                short numId = buf.readShort();
                CscPacketFactory<?> factory = CscPackets.getByNumId(numId).factory();
                try {
                    if (factory == null) {
                        LOGGER.warn("Received unknown packet with, code: {}", numId);
                        return new CscPacketWrapper(new UnknownPacket(buf));
                    }
                    CscPacket packet = factory.create(buf);
                    return new CscPacketWrapper(packet);
                } catch (Exception e) {
                    throw new SkipPacketDecoderException(e);
                }

            }
    );

    private final HashMap<CscPacketType<?>, List<CscClientService.ClientPacketListener>> listHashMap = new HashMap<>();
    private final HashMap<CscPacketType<?>, List<CscClientService.ClientSinglePacketListener<?>>> typedListeners = new HashMap<>();

    public FabricClientService() {
        PayloadTypeRegistry.playC2S().register(TYPE, CODEC);
        PayloadTypeRegistry.playS2C().register(TYPE, CODEC);
        ClientPlayNetworking.registerGlobalReceiver(TYPE, ((payload, context) ->  {
            CscPacket packet = payload.packet();
            CscPacketType<?> packetType = CscPackets.getByClass(packet.getClass());

            List<CscClientService.ClientPacketListener> listeners = listHashMap.get(packetType);
            if (listeners != null) {
                listeners.forEach(l -> l.receive(packet));
            }

            List<CscClientService.ClientSinglePacketListener<?>> typedList = typedListeners.get(packetType);
            if (typedList != null) {
                typedList.forEach(l -> invokeListener(l, packet));
            }
        }));
    }

    @SuppressWarnings("unchecked")
    private <T extends CscPacket> void invokeListener(CscClientService.ClientSinglePacketListener<T> listener, CscPacket packet) {
        listener.receive((T) packet);
    }

    @Override
    public void sendPacket(CscPacket packet) {
        ClientPlayNetworking.send(new CscPacketWrapper(packet));
    }

    @Override
    public void registerListener(CscClientService.ClientPacketListener listener, CscPacketType<?>... packets) {
        for (CscPacketType<?> type : packets) {
            listHashMap.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
        }
    }

    @Override
    public <A extends CscPacket> void registerListener(CscClientService.ClientSinglePacketListener<A> listener, CscPacketType<A> type) {
        typedListeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    private record CscPacketWrapper(CscPacket packet) implements CustomPacketPayload {
        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
