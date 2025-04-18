package org.gz.wlrt.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RedstoneView;
import net.minecraft.world.World;
import org.gz.wlrt.utils.GlobalBlockPos;
import org.gz.wlrt.utils.Manager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RedstoneView.class)
public interface RedstoneViewMixin {
    @WrapOperation(
            method = "getReceivedRedstonePower",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/RedstoneView;getEmittedRedstonePower(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)I"))
    private int getReceivedRedstonePower(RedstoneView instance, BlockPos pos, Direction direction, Operation<Integer> original) {
        return getEmittedRedstonePower(instance, pos, direction, original);
    }

    @WrapOperation(
            method = "isReceivingRedstonePower",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/RedstoneView;getEmittedRedstonePower(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)I"))
    private int isReceivingRedstonePower(RedstoneView instance, BlockPos pos, Direction direction, Operation<Integer> original) {
        return getEmittedRedstonePower(instance, pos, direction, original);
    }

    @Unique
    private int getEmittedRedstonePower(RedstoneView instance, BlockPos pos, Direction direction, Operation<Integer> original) {
        if (instance instanceof ServerWorld world && !world.isClient) {
            GlobalBlockPos output = new GlobalBlockPos(world.getRegistryKey(), pos.offset(direction.getOpposite()));
            if (Manager.isLinkedOutput(output)) {
                return getInputSignalFor(output, direction.getOpposite(), world);
            }
        }
        return original.call(instance, pos, direction);
    }

    @Unique
    private int getInputSignalFor(GlobalBlockPos pos, Direction direction, ServerWorld w) {
        World world = pos.getWorld(w);
        GlobalBlockPos source = Manager.getSource(pos);
        return world.getEmittedRedstonePower(source, direction);
    }

}
