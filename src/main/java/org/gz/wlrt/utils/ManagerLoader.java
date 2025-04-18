package org.gz.wlrt.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.WorldSavePath;

public class ManagerLoader {
    @Environment(EnvType.CLIENT)
    public static void loadForIntegratedServer() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.getServer() != null) { // Check if the client is connected to an integrated server.
                Manager.load(client.getServer().getSavePath(WorldSavePath.ROOT));
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (client.getServer() != null) { // Check if the client is connected to an integrated server.
                Manager.save(client.getServer().getSavePath(WorldSavePath.ROOT));
                Manager.clear();
            }
        });
    }

    @Environment(EnvType.SERVER)
    public static void loadForRemoteServer() {
        ServerPlayConnectionEvents.INIT.register(
                (handler, server) -> Manager.load(server.getSavePath(WorldSavePath.ROOT)));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            Manager.save(server.getSavePath(WorldSavePath.ROOT));
            Manager.clear();
        });
    }
}
