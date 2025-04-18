package org.gz.wlrt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.gz.wlrt.item.LinkWand;
import org.gz.wlrt.utils.ManagerLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wlrt implements ModInitializer {
	public static final String MOD_ID = "wlrt";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Logger QBZ = LoggerFactory.getLogger("debug");

    @Override
	public void onInitialize() {
		LinkWand.register();
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER)
			ManagerLoader.loadForRemoteServer();
		else ManagerLoader.loadForIntegratedServer();
	}
}