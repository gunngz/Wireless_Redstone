package org.gz.wlrt;

import net.fabricmc.api.ClientModInitializer;
import org.gz.wlrt.utils.BlockOutlineRenderer;

public class WlrtClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockOutlineRenderer.register();
    }

}
