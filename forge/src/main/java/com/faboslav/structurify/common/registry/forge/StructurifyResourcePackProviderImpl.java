package com.faboslav.structurify.common.registry.forge;

import net.minecraft.resource.ResourcePackProvider;

import java.util.ArrayList;

/**
 * Forge injects stuff into the vanilla registry
 */
public final class StructurifyResourcePackProviderImpl
{
	public static ArrayList<ResourcePackProvider> getPlatformResourcePackProviders() {
		ArrayList<ResourcePackProvider> platformResourcePackProviders = new ArrayList<>();
		return platformResourcePackProviders;
	}
}
