package org.gz.wlrt.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.gz.wlrt.Wlrt;
import org.gz.wlrt.utils.GlobalBlockPos;
import org.gz.wlrt.utils.Manager;


public class LinkWand extends Item {
    public static final LinkWand LINK_WAND = Registry.register(Registries.ITEM, new Identifier(Wlrt.MOD_ID, "link_wand"), new LinkWand(new Item.Settings()));

    public LinkWand(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        if (ctx.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }

        GlobalBlockPos pos = GlobalBlockPos.from(ctx);
        String identifier = pos.toString();
        if (Manager.isLinkedOutput(pos)) {
            sendMessage(ctx, "remove_successfully", identifier);
            Manager.removeByOutput(pos);
            return ActionResult.SUCCESS;
        }
        if (!hadSetSource(ctx.getStack())) {
            if (!canEmitRedstone(ctx.getWorld(), pos)) {
                sendMessage(ctx, "fail_to_set_source", identifier);
                return ActionResult.FAIL;
            }
            sendMessage(ctx, "set_source_to", identifier);
            setSource(pos, ctx.getStack());
        } else {
            // pos -> output
            sendMessage(ctx, "set_output_to", identifier);
            GlobalBlockPos source = GlobalBlockPos.from(ctx.getStack().getNbt().getString("source"));
            addToManager(pos, source);
            updateNeighbor(ctx.getWorld(), pos, source);
            clearNbt(ctx.getStack());
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            super.inventoryTick(stack, world, entity, slot, selected);
            return;
        }
        if (!selected) {
            Manager.setLookingAt(null);
            return;
        }
        if (entity instanceof PlayerEntity) {
            HitResult hit = MinecraftClient.getInstance().crosshairTarget;
            if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                GlobalBlockPos lookingAt = new GlobalBlockPos(
                        world.getRegistryKey(),
                        ((BlockHitResult) hit).getBlockPos());
                Manager.setLookingAt(lookingAt);
            }
        }
    }

    private void updateNeighbor(World world, BlockPos pos, BlockPos sourcePos) {
        world.updateNeighbor(pos, world.getBlockState(pos).getBlock(), sourcePos);
    }

    private void clearNbt(ItemStack stack) {
        if (stack.getNbt() != null) {
            stack.getNbt().remove("source");
        }
    }

    private void sendMessage(ItemUsageContext ctx, String s, Object... args) {
        PlayerEntity player = ctx.getPlayer();
        if (player != null) {
            player.sendMessage(Text.translatable("text.wlrt." + s, args), true);
        }
    }

    private boolean canEmitRedstone(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock().emitsRedstonePower(world.getBlockState(pos));
    }

    private void addToManager(GlobalBlockPos pos, GlobalBlockPos source) {
        Manager.add(pos, source);
    }

    private void setSource(GlobalBlockPos pos, ItemStack stack) {
        stack.getNbt().putString("source", pos.toString());
    }

    private boolean hadSetSource(ItemStack stack) {
        return stack.getOrCreateNbt().contains("source");
    }

    static {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.add(LINK_WAND));
    }

    public static void register() {}
}
