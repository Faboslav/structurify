package com.faboslav.structurify.forge.platform;

import com.faboslav.structurify.common.platform.PlatformResourcePackProvider;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;

/**
 * Forge injects stuff into the vanilla registry
 */
public final class ForgePlatformResourcePackProvider implements PlatformResourcePackProvider
{
	@Override
	public ArrayList<RepositorySource> getPlatformResourcePackProviders() {
		ArrayList<RepositorySource> platformResourcePackProviders = new ArrayList<>();
		return platformResourcePackProviders;
	}
}
