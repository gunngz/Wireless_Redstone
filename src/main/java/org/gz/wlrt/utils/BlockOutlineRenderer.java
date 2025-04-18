package org.gz.wlrt.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import static org.gz.wlrt.Wlrt.QBZ;

public class BlockOutlineRenderer {
    public static void register() {
        WorldRenderEvents.BEFORE_ENTITIES.register(ctx -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return;
            GlobalBlockPos highlightPos = Manager.getLookingAt();
            if (Manager.isLinkedSource(highlightPos)) {
                highlight(ctx, highlightPos);
                Manager.getOutputs(highlightPos).stream().filter(p -> p.isIn(ctx.world())).forEach(
                        output -> highlight(ctx, output));
            } else if (Manager.isLinkedOutput(highlightPos)) {
                highlight(ctx, highlightPos);
                GlobalBlockPos source = Manager.getSource(highlightPos);
                if (!source.isIn(ctx.world())) return;
                highlight(ctx, source);
            }
        });

    }

    private static void highlight(WorldRenderContext ctx, GlobalBlockPos highlightPos) {
        MatrixStack matrices = ctx.matrixStack();
        Camera camera = ctx.camera();
        VertexConsumerProvider vertexConsumers = ctx.consumers();

        Vec3d cameraPos = camera.getPos();
        Box box = new Box(highlightPos).offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        WorldRenderer.drawBox(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getLines()),
                box,
                123f / 255f, 195f / 255f, 92f / 255f, 1f
        );
        WorldRenderer.drawBox(
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getLines()),
                box.expand(0.0025),
                123f / 255f, 195f / 255f, 92f / 255f, 1f
        );
    }
}