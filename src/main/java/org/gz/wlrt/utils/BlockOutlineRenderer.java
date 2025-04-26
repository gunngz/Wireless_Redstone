package org.gz.wlrt.utils;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ClientOnly
public class BlockOutlineRenderer {
    public static final Method DRAW_CUBOID_SHAPE_OUTLINE;

    static {
        try {
            DRAW_CUBOID_SHAPE_OUTLINE = WorldRenderer.class.getDeclaredMethod("drawCuboidShapeOutline", MatrixStack.class, VertexConsumer.class, VoxelShape.class, double.class, double.class, double.class, float.class, float.class, float.class, float.class);
            DRAW_CUBOID_SHAPE_OUTLINE.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void register() {
        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, hit) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return true;
            Manager.setAllLinked(client.world);
            if (Manager.allLinked.isEmpty()) return true;
            for (var pos : Manager.allLinked) {
                highlight(ctx, pos);
            }
            return false;
        });
    }

    private static void highlight(WorldRenderContext ctx, GlobalBlockPos pos) {
        MatrixStack matrices = ctx.matrixStack();
        Camera camera = ctx.camera();
        VertexConsumerProvider vertexConsumers = ctx.consumers();

        Vec3d cameraPos = camera.getPos();
        ClientWorld world = MinecraftClient.getInstance().world;

        if (world == null) return;

        BlockState state = world.getBlockState(pos);
        VoxelShape shape = state.getOutlineShape(world, pos);

        assert vertexConsumers != null;

        try {
            DRAW_CUBOID_SHAPE_OUTLINE.invoke(
                    null,
                    matrices,
                    vertexConsumers.getBuffer(RenderLayer.getLines()),
                    shape,
                    pos.getX() - cameraPos.x,
                    pos.getY() - cameraPos.y,
                    pos.getZ() - cameraPos.z,
                    123f / 255f, 195f / 255f, 92f / 255f, 1f);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}