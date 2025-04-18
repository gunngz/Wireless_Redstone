package org.gz.wlrt.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import org.gz.wlrt.item.LinkWand;

import java.util.function.Consumer;

public class WlrtRecipeProvider extends FabricRecipeProvider {
    public WlrtRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> consumer) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, LinkWand.LINK_WAND, 1)
                .input('s', Items.STICK)
                .input('r', Items.REDSTONE_BLOCK)
                .input('e', Items.ENDER_EYE)
                .pattern(" er")
                .pattern(" se")
                .pattern("s  ")
                .criterion(hasItem(Items.ENDER_EYE), conditionsFromItem(Items.ENDER_EYE))
                .group("wand")
                .offerTo(consumer, "link_wand");
    }
}
