package org.gz.wlrt.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import org.gz.wlrt.item.LinkWand;

public class WlrtLangProvider extends FabricLanguageProvider {
    protected WlrtLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        builder.add(LinkWand.LINK_WAND, "Link Wand");

        builder.add("text.wlrt.set_source_to", "Set source to %s");
        builder.add("text.wlrt.set_output_to", "Set output to %s");
        builder.add("text.wlrt.remove_successfully", "Removed %s successfully");
        builder.add("text.wlrt.fail_to_set_source", "Failed to set source %s");
    }
}
