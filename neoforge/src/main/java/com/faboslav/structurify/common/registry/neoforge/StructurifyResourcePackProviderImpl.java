package com.faboslav.structurify.common.registry.neoforge;

import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;

/**
 * NeoForge injects stuff into the vanilla registry
 */
public final class StructurifyResourcePackProviderImpl
{
	public static ArrayList<RepositorySource> getPlatformResourcePackProviders() {
		ArrayList<RepositorySource> platformResourcePackProviders = new ArrayList<>();
		return platformResourcePackProviders;
	}
}
