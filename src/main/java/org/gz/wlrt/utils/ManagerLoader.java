package org.gz.wlrt.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

public class ManagerLoader {
    public static @Nullable MinecraftServer server = null;
    @Environment(EnvType.CLIENT)
    public static void loadForIntegratedServer() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.getServer() != null) { // Check if the client is connected to an integrated server.
                Manager.load(client.getServer().getSavePath(WorldSavePath.ROOT));
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (client.getServer() != null) { // Check if the client is connected to an integrated server.
                Manager.clear();
            }
        });
    }

    @Environment(EnvType.SERVER)
    public static void loadForRemoteServer() {
        ServerLifecycleEvents.SERVER_STARTING.register((theServer) -> {
            server = theServer;
            Manager.load(server.getSavePath(WorldSavePath.ROOT));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            sender.sendPacket(Connections.INIT, Manager.toPacketByteBuf());
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            Manager.save(server.getSavePath(WorldSavePath.ROOT));
            Manager.clear();
        });
    }

}
