package com.faboslav.structurify.common.modcompat.fabric;

import com.faboslav.structurify.common.config.client.gui.StructurifyConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;

public final class CatalogueCompat
{
	public static Screen createConfigScreen(Screen currentScreen, ModContainer container) {
		if (
			!FabricLoader.getInstance().isModLoaded("catalogue")
			|| !FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")
		) {
			return null;
		}

		return new StructurifyConfigScreen(currentScreen);
	}
}
