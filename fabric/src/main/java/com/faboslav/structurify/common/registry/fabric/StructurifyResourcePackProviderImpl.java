package com.faboslav.structurify.common.registry.fabric;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;

import java.util.ArrayList;

public final class StructurifyResourcePackProviderImpl
{
	public static ArrayList<ResourcePackProvider> getPlatformResourcePackProviders() {
		ArrayList<ResourcePackProvider> platformResourcePackProviders = new ArrayList<>();

		platformResourcePackProviders.add(new ModResourcePackCreator(ResourceType.SERVER_DATA));

		return platformResourcePackProviders;
	}
}
