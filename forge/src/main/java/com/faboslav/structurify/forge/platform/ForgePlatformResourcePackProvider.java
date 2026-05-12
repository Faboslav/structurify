package com.faboslav.structurify.forge.platform;

import com.faboslav.structurify.common.platform.PlatformResourcePackProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.resource.ResourcePackLoader;

import java.util.ArrayList;

/**
 * Forge injects stuff into the vanilla registry
 */
public final class ForgePlatformResourcePackProvider implements PlatformResourcePackProvider
{
	@Override
	public void loadPlatformResourcePacks(PackRepository resourcePackManager) {
		ResourcePackLoader.loadResourcePacks(resourcePackManager, modResourcePacks -> consumer -> {
			for (var entry : modResourcePacks.entrySet()) {
				var modFile = entry.getKey();
				var packResources = entry.getValue();
				var modId = modFile.getModFileInfo().getMods().get(0).getModId();
				consumer.accept(Pack.readMetaAndCreate(
					"mod:" + modId,
					Component.literal(modId),
					true,
					id -> packResources,
					PackType.SERVER_DATA,
					Pack.Position.BOTTOM,
					PackSource.BUILT_IN
				));
			}
		});

	}
}
