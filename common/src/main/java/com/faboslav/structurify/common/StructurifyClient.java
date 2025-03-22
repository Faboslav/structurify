package com.faboslav.structurify.common;

import com.faboslav.structurify.common.config.client.gui.StructurifyConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class StructurifyClient
{
	private static StructurifyConfigScreen CONFIG_SCREEN;

	public static void init() {
	}

	public static StructurifyConfigScreen getConfigScreen(Screen screen) {
		if(CONFIG_SCREEN == null) {
			CONFIG_SCREEN = new StructurifyConfigScreen(screen);
		}

		return CONFIG_SCREEN;
	}

	public static StructurifyConfigScreen getConfigScreen() {
		return CONFIG_SCREEN;
	}
}
