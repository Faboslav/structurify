package com.faboslav.structurify.forge.platform;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.platform.PlatformHelper;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

public final class ForgePlatformHelper implements PlatformHelper
{
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	@Nullable
	public String getModVersion() {
		return ModList.get().getModContainerById(Structurify.MOD_ID).map(modContainer -> modContainer.getModInfo().getVersion().toString()).orElse(null);
	}
}
