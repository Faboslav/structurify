package com.faboslav.structurify.common.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Nullable;

public final class Platform
{
	@ExpectPlatform
	public static boolean isModLoaded(String modId) {
		throw new AssertionError();
	}

	@ExpectPlatform
	@Nullable
	public static String getModVersion() {
		throw new AssertionError();
	}
}

