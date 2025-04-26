package org.gz.wlrt;

import net.fabricmc.api.ClientModInitializer;
import org.gz.wlrt.utils.BlockOutlineRenderer;
import org.gz.wlrt.utils.Connections;

public class WlrtClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockOutlineRenderer.register();
        Connections.registerClient();
    }

}
