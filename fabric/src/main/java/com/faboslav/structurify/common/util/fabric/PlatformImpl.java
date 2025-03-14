package com.faboslav.structurify.common.util.fabric;

import com.faboslav.structurify.common.Structurify;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public final class PlatformImpl
{
	public static boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Nullable
	public static String getModVersion() {
		return FabricLoader.getInstance().getModContainer(Structurify.MOD_ID).map(modContainer -> modContainer.getMetadata().getVersion().toString()).orElse(null);
	}

	public static Path getConfigDirectory() {
		return FabricLoader.getInstance().getConfigDir();
	}

	private PlatformImpl() {
	}
}
