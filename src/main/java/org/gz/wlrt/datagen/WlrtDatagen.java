package org.gz.wlrt.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WlrtDatagen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator gen) {
		FabricDataGenerator.Pack pack = gen.createPack();
		pack.addProvider(WlrtModelProvider::new);
		pack.addProvider(WlrtLangProvider::new);
		pack.addProvider(WlrtRecipeProvider::new);
	}
}
