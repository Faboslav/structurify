package com.faboslav.structurify.common.modcompat.forge;

import com.faboslav.structurify.common.Structurify;

import static com.faboslav.structurify.common.modcompat.ModChecker.loadModCompat;

public final class ModCheckerImpl
{
	public static void setupPlatformModCompat() {
		String modId = "";

		try {
		} catch (Throwable e) {
			Structurify.getLogger().error("Failed to setup compat with " + modId);
			e.printStackTrace();
		}
	}
}
