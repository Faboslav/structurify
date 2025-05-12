package com.faboslav.structurify.fabric.platform;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.platform.PlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricPlatformHelper implements PlatformHelper
{
	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public String getModVersion() {
		return FabricLoader.getInstance().getModContainer(Structurify.MOD_ID).map(modContainer -> modContainer.getMetadata().getVersion().toString()).orElse(null);
	}
}
