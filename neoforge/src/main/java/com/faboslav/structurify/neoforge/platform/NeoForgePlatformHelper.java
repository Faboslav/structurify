package com.faboslav.structurify.neoforge.platform;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.platform.ModIconInfo;
import com.faboslav.structurify.common.platform.PlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public final class NeoForgePlatformHelper implements PlatformHelper
{
	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public Optional<ModIconInfo> getModIconInfo(String modId) {
		return PlatformHelper.MOD_ICON_INFO_CACHE.computeIfAbsent(modId, id -> {
			var modContainer = ModList.get().getModContainerById(id);

			if (modContainer.isEmpty()) {
				return Optional.empty();
			}

			var iconPath = modContainer.get().getModInfo().getLogoFile();

			if (iconPath.isEmpty()) {
				return Optional.empty();
			}


			//? if >= 1.21.10 {
			try {
				return getModIconInfo(id, iconPath, modContainer.get().getModInfo().getOwningFile().getFile().getContents().openFile(iconPath.get()));
			} catch (IOException e) {
				return Optional.empty();
			}
			//?} else {
			/*return getModIconInfo(id, iconPath, Optional.of(modContainer.get().getModInfo().getOwningFile().getFile().findResource(iconPath.get())));
			*///?}
		});
	}

	@Override
	@Nullable
	public String getModVersion() {
		return ModList.get().getModContainerById(Structurify.MOD_ID).map(modContainer -> modContainer.getModInfo().getVersion().toString()).orElse(null);
	}

	@Override
	public Path getConfigDirectory() {
		return FMLPaths.CONFIGDIR.get();
	}
}
