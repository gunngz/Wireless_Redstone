package org.gz.wlrt.utils;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import static org.gz.wlrt.Wlrt.MOD_ID;

public class Connections {
    public static final Identifier INIT = new Identifier(MOD_ID, "init");
    public static final Identifier UPDATE_ADD = new Identifier(MOD_ID, "update_add");
    public static final Identifier UPDATE_REMOVE_BY_SOURCE = new Identifier(MOD_ID, "update_remove_by_source");
    public static final Identifier UPDATE_REMOVE_BY_OUTPUT = new Identifier(MOD_ID, "update_remove_by_output");

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(INIT, (client, handler, buf, responseSender) -> {
            Manager.fromPacketByteBuf(buf);
        });
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_ADD, (client, handler, buf, responseSender) -> {
            GlobalBlockPos pos1 = GlobalBlockPos.from(buf.readString());
            GlobalBlockPos pos2 = GlobalBlockPos.from(buf.readString());
            Manager.add(pos1, pos2);
        });
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_REMOVE_BY_SOURCE, (client, handler, buf, responseSender) -> {
            GlobalBlockPos pos1 = GlobalBlockPos.from(buf.readString());
            GlobalBlockPos pos2 = GlobalBlockPos.from(buf.readString());
            Manager.add(pos1, pos2);
        });
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_REMOVE_BY_OUTPUT, (client, handler, buf, responseSender) -> {
            GlobalBlockPos pos1 = GlobalBlockPos.from(buf.readString());
            GlobalBlockPos pos2 = GlobalBlockPos.from(buf.readString());
            Manager.add(pos1, pos2);
        });
    }

    public static void sendIfInServer(Identifier updateRemoveByOutput, GlobalBlockPos removedBlock) {
        if (ManagerLoader.server != null) {
            ManagerLoader.server.getPlayerManager().getPlayerList().forEach(player -> {
                ServerPlayNetworking.send(player, updateRemoveByOutput,
                        PacketByteBufs.create()
                                .writeString(removedBlock.toString()));
            });
        }
    }
}
