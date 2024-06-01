package com.faboslav.structurify.common.util;

import dev.architectury.injectables.annotations.ExpectPlatform;

public final class Platform
{
	@ExpectPlatform
	public static boolean isModLoaded(String modId) {
		throw new AssertionError();
	}
}

