package com.faboslav.structurify.common.registry.neoforge;

import net.minecraft.resource.ResourcePackProvider;

import java.util.ArrayList;

/**
 * NeoForge injects stuff into the vanilla registry
 */
public final class StructurifyResourcePackProviderImpl
{
	public static ArrayList<ResourcePackProvider> getPlatformResourcePackProviders() {
		ArrayList<ResourcePackProvider> platformResourcePackProviders = new ArrayList<>();
		return platformResourcePackProviders;
	}
}
