package com.faboslav.structurify.common.util.forge;

import com.faboslav.structurify.common.Structurify;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public final class PlatformImpl
{
	public static boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Nullable
	public static String getModVersion() {
		return ModList.get().getModContainerById(Structurify.MOD_ID).map(modContainer -> modContainer.getModInfo().getVersion().toString()).orElse(null);
	}

	public static Path getConfigDirectory() {
		return FMLPaths.CONFIGDIR.get();
	}

	private PlatformImpl() {
	}
}
