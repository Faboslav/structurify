package com.faboslav.structurify.forge.platform;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.platform.PlatformCompat;

public final class ForgePlatformCompat implements PlatformCompat
{
	@Override
	public void setupPlatformModCompat() {
		String modId = "";

		try {
		} catch (Throwable e) {
			Structurify.getLogger().error("Failed to setup compat with " + modId);
			e.printStackTrace();
		}
	}
}
