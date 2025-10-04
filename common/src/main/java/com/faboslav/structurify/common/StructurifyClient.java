package com.faboslav.structurify.common;

import com.faboslav.structurify.common.config.client.gui.StructurifyConfigScreen;
import com.faboslav.structurify.common.debug.StructurifyDebugRenderer;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public final class StructurifyClient
{
	private static StructurifyConfigScreen CONFIG_SCREEN;
	private static final StructurifyDebugRenderer DEBUG_RENDERER = new StructurifyDebugRenderer();

	public static StructurifyDebugRenderer getDebugRenderer() {
		return DEBUG_RENDERER;
	}

	public static void init() {
	}

	public static StructurifyConfigScreen getConfigScreen(Screen screen) {
		if (CONFIG_SCREEN == null) {
			CONFIG_SCREEN = new StructurifyConfigScreen(screen);
		}

		CONFIG_SCREEN.setParent(screen);

		return CONFIG_SCREEN;
	}

	@Nullable
	public static StructurifyConfigScreen getConfigScreen() {
		return CONFIG_SCREEN;
	}
}
