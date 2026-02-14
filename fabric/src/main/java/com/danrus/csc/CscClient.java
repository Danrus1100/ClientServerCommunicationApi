package com.danrus.csc;

import com.danrus.csc.api.client.CscClientService;
import net.fabricmc.api.ClientModInitializer;

public class CscClient implements ClientModInitializer {
    private static final String MOD_ID = "csc";
    private static final CscClientService SERVICE = new FabricClientService();

    @Override
    public void onInitializeClient() {}

    public static CscClientService getService() {
        return SERVICE;
    }
}
