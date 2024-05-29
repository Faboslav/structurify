package com.faboslav.structurized.fabric.modcompat;

import com.faboslav.structurized.common.Structurized;
import com.faboslav.structurized.common.config.gui.StructurizedConfigGui;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public final class ModMenuCompat implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (parent) -> {
			if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3") == false) {
				return null;
			}

			return StructurizedConfigGui.createConfigGui(Structurized.getConfig(), parent);
		};
	}
}