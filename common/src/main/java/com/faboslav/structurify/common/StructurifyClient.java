package com.faboslav.structurify.common;

import com.faboslav.structurify.common.config.client.gui.StructurifyConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public final class StructurifyClient
{
	private static StructurifyConfigScreen CONFIG_SCREEN;

	public static void init() {
	}

	public static StructurifyConfigScreen getConfigScreen(Screen screen) {
		if (CONFIG_SCREEN == null) {
			CONFIG_SCREEN = new StructurifyConfigScreen(screen);
		}

		return CONFIG_SCREEN;
	}

	@Nullable
	public static StructurifyConfigScreen getConfigScreen() {
		return CONFIG_SCREEN;
	}
}
