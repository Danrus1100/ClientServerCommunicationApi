package com.danrus.csc;

import com.danrus.csc.api.server.CscServerService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CscServer extends JavaPlugin {

    private static CscServerService<Player> SERVICE;

    @Override
    public void onEnable() {
        SERVICE = new CscPaperService(this);
    }

    public static CscServerService<Player> getService() {
        return SERVICE;
    }
}
