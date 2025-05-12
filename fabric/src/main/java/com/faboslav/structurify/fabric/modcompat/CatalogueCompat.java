package com.faboslav.structurify.fabric.modcompat;

import com.faboslav.structurify.common.StructurifyClient;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;

public final class CatalogueCompat
{
	public static Screen createConfigScreen(Screen currentScreen, ModContainer container) {
		if (
			!FabricLoader.getInstance().isModLoaded("catalogue")
		) {
			return null;
		}

		return StructurifyClient.getConfigScreen(currentScreen);
	}
}
