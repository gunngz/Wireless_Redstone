package org.gz.wlrt.utils;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class GlobalBlockPos extends BlockPos {
    protected final RegistryKey<World> worldKey;

    public GlobalBlockPos(RegistryKey<World> worldKey, BlockPos pos) {
        super(pos.getX(), pos.getY(), pos.getZ());
        this.worldKey = worldKey;
    }

    public static GlobalBlockPos from(ItemUsageContext ctx) {
        return new GlobalBlockPos(ctx.getWorld().getRegistryKey(), ctx.getBlockPos());
    }

    public static GlobalBlockPos from(String str) {
        String[] parts = str.split(":");
        assert parts.length == 5 : "Invalid GlobalBlockPos string: " + str;
        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(parts[0], parts[1]));
        BlockPos pos = new BlockPos(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
        return new GlobalBlockPos(worldKey, pos);
    }

    @Override
    public String toString() {
        return worldKey.getValue().getNamespace() + ":" + worldKey.getValue().getPath() + ":" + getX() + ":" + getY() + ":" + getZ();
    }

    public String toString(World world) {
        return world.getBlockState(this).toString() + " at " + toString();
    }


    public boolean isIn(World world) {
        return this.worldKey.equals(world.getRegistryKey());
    }

    public ServerWorld getWorld(ServerWorld world) {
        return world.getServer().getWorld(this.worldKey);
    }

    public int getChunkX() {
        return getX() >> 4;
    }

    public int getChunkZ() {
        return getZ() >> 4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GlobalBlockPos that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(worldKey, that.worldKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), worldKey);
    }
}