package com.faboslav.structurify.common.modcompat.fabric;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.gui.StructurifyConfigScreen;
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

			return StructurifyConfigScreen.createConfigGui(Structurify.getConfig(), parent);
		};
	}
}