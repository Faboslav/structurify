package com.faboslav.structurify.fabric.platform;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.platform.ModIconInfo;
import com.faboslav.structurify.common.platform.PlatformHelper;
import com.faboslav.structurify.common.util.FileUtil;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.Optional;

public final class FabricPlatformHelper implements PlatformHelper
{
	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public Optional<ModIconInfo> getModIconInfo(String modId) {
		return PlatformHelper.MOD_ICON_INFO_CACHE.computeIfAbsent(modId, id -> {
			var modContainer = FabricLoader.getInstance().getModContainer(id);

			if(modContainer.isEmpty()) {
				return Optional.empty();
			}

			var iconPath = modContainer.get().getMetadata().getIconPath(128);

			if(iconPath.isEmpty()) {
				return Optional.empty();
			}

			return FileUtil.getModIconInfo(id, iconPath, modContainer.get().findPath(iconPath.get()));
		});
	}

	@Override
	public String getModVersion() {
		return FabricLoader.getInstance().getModContainer(Structurify.MOD_ID).map(modContainer -> modContainer.getMetadata().getVersion().toString()).orElse(null);
	}

	@Override
	public Path getConfigDirectory() {
		return FabricLoader.getInstance().getConfigDir();
	}
}
