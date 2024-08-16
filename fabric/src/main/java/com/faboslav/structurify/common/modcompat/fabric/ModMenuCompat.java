package com.faboslav.structurify.common.modcompat.fabric;

import com.faboslav.structurify.common.config.client.gui.StructurifyConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public final class ModMenuCompat implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (screen) -> {
			if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3") == false) {
				return null;
			}

			return new StructurifyConfigScreen(screen);
		};
	}
}