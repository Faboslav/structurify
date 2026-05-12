package com.faboslav.structurify.forge.platform;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.platform.ModIconInfo;
import com.faboslav.structurify.common.platform.PlatformHelper;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import com.faboslav.structurify.common.util.FileUtil;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class ForgePlatformHelper implements PlatformHelper
{
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public Optional<ModIconInfo> getModIconInfo(String modId) {
		return PlatformHelper.MOD_ICON_INFO_CACHE.computeIfAbsent(modId, id -> {
			var modContainer = ModList.get().getModContainerById(id);

			if(modContainer.isEmpty()) {
				return Optional.empty();
			}

			var iconPath = modContainer.get().getModInfo().getLogoFile();

			if(iconPath.isEmpty()) {
				return Optional.empty();
			}

			return FileUtil.getModIconInfo(id, iconPath, Optional.of(modContainer.get().getModInfo().getOwningFile().getFile().findResource(iconPath.get())));
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
