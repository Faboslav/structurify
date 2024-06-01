package com.faboslav.structurify.common.util.forge;

import net.minecraftforge.fml.ModList;

public final class PlatformImpl
{
	public static boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	private PlatformImpl() {
	}
}
