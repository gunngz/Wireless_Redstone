package org.gz.wlrt.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import org.gz.wlrt.item.LinkWand;

public class WlrtModelProvider extends FabricModelProvider {
    public WlrtModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator gen) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator gen) {
        gen.register(LinkWand.LINK_WAND, Models.HANDHELD);
    }
}
