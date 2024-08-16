package com.faboslav.structurify.common.util.neoforge;

import net.neoforged.fml.ModList;

public final class PlatformImpl
{
	public static boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	private PlatformImpl() {
	}
}
