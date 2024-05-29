package com.faboslav.structurized.fabric.modcompat;

import com.faboslav.structurized.common.Structurized;
import com.faboslav.structurized.common.config.gui.StructurizedConfigGui;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screen.Screen;

public final class CatalogueCompat
{
	public static Screen createConfigScreen(Screen currentScreen, ModContainer container) {
		if (
			FabricLoader.getInstance().isModLoaded("catalogue") == false
			|| FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3") == false
		) {
			return null;
		}

		return StructurizedConfigGui.createConfigGui(Structurized.getConfig(), currentScreen);
	}
}
