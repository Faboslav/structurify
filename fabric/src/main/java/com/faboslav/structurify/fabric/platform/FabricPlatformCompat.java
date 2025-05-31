package com.faboslav.structurify.fabric.platform;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.platform.PlatformCompat;
import com.faboslav.structurify.fabric.modcompat.GlobalDatapacksCompat;
import com.faboslav.structurify.fabric.modcompat.TerraCompat;

import static com.faboslav.structurify.common.modcompat.ModChecker.loadModCompat;

public final class FabricPlatformCompat implements PlatformCompat
{
	@Override
	public void setupPlatformModCompat() {
		String modId = "";

		try {
			modId = "global-datapack";
			loadModCompat(modId, () -> new GlobalDatapacksCompat());

			modId = "terra";
			//? terra {
			/*loadModCompat("terra", () -> new TerraCompat());
			*///?}
		} catch (Throwable e) {
			Structurify.getLogger().error("Failed to setup compat with " + modId);
			e.printStackTrace();
		}
	}
}
