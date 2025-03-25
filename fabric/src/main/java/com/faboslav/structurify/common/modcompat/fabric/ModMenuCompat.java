package com.faboslav.structurify.common.modcompat.fabric;

import com.faboslav.structurify.common.StructurifyClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public final class ModMenuCompat implements ModMenuApi
{
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (screen) -> {
			if (
				!FabricLoader.getInstance().isModLoaded("modmenu")
			) {
				return null;
			}

			return StructurifyClient.getConfigScreen(screen);
		};
	}
}