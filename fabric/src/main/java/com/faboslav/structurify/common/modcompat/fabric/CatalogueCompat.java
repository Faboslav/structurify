package com.faboslav.structurify.common.modcompat.fabric;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.client.gui.StructuresConfigScreen;
import com.faboslav.structurify.common.config.client.gui.StructurifyConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screen.Screen;

public final class CatalogueCompat
{
	public static Screen createConfigScreen(Screen screen, ModContainer container) {
		if (
			FabricLoader.getInstance().isModLoaded("catalogue") == false
			|| FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3") == false
		) {
			return null;
		}

		return new StructurifyConfigScreen(screen);
	}
}
