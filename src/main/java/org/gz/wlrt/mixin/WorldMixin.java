package org.gz.wlrt.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gz.wlrt.Wlrt;
import org.gz.wlrt.utils.GlobalBlockPos;
import org.gz.wlrt.utils.Manager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow public abstract RegistryKey<World> getRegistryKey();
    @Shadow @Final public boolean isClient;

    @Inject(
            method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
            at = @At("RETURN"))
    private void onSetBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        if (isClient) {
            return;
        }

        if (!(((World) (Object) this) instanceof ServerWorld world)) {
            return;
        }
        GlobalBlockPos source = new GlobalBlockPos(getRegistryKey(), pos);
        if (Manager.isLinkedSource(source)) {
            for (GlobalBlockPos output : Manager.getOutputs(source)) {
                World outputWorld = output.getWorld(world);
                outputWorld.updateNeighbor(output, state.getBlock(), pos);
            }
        }
    }

    @Inject(
            method = "removeBlock",
            at = @At("RETURN"))
    private void onRemoveBlock(BlockPos pos, boolean move, CallbackInfoReturnable<Boolean> cir) {
        removeLink(pos);
    }

    @Inject(
            method = "breakBlock",
            at = @At("RETURN"))
    private void onBreakBlock(BlockPos pos, boolean drop, Entity breakingEntity, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        removeLink(pos);
    }

    @Unique
    private void removeLink(BlockPos pos) {
        GlobalBlockPos removedBlock = new GlobalBlockPos(getRegistryKey(), pos);
        if (isClient) {
            return;
        }

        if (!(((World) (Object) this) instanceof ServerWorld world)) {
            return;
        }

        if (Manager.isLinkedSource(removedBlock)) {
            for (GlobalBlockPos output : Manager.getOutputs(removedBlock)) {
                // Here ok.
                output.getWorld(world)
                        .updateNeighbor(output, world.getBlockState(removedBlock).getBlock(), removedBlock);
            }
            Manager.removeBySource(removedBlock);
        } else if (Manager.isLinkedOutput(removedBlock)) {
            Manager.removeByOutput(removedBlock);
        }
    }

}
