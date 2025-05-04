package org.gz.wlrt.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import org.gz.wlrt.Wlrt;
import org.gz.wlrt.utils.Manager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class PauseScreenMixin {
    @Inject(
            method = "init",
            at = @At("RETURN"))
    protected void onInit(CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getServer() != null) { // Check if the client is connected to an integrated server.
            Manager.save(client.getServer().getSavePath(WorldSavePath.ROOT));
        }
    }
}
