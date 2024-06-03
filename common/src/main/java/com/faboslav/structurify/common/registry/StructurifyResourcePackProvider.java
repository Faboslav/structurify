package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.modcompat.ModChecker;
import com.faboslav.structurify.common.modcompat.ModCompat;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.VanillaDataPackProvider;

import java.util.ArrayList;

public final class StructurifyResourcePackProvider
{
	public static ArrayList<ResourcePackProvider> getResourcePackProviders() {
		ArrayList<ResourcePackProvider> resourcePackProviders = new ArrayList<>();

		resourcePackProviders.addAll(getVanillaResourcePackProviders());
		resourcePackProviders.addAll(getPlatformResourcePackProviders());
		resourcePackProviders.addAll(getModsResourcePackProviders());

		return resourcePackProviders;
	}

	public static ArrayList<ResourcePackProvider> getVanillaResourcePackProviders() {
		ArrayList<ResourcePackProvider> vanillaResourcePackProviders = new ArrayList<>();

		/*? 1.20.1 {*/
		vanillaResourcePackProviders.add(new VanillaDataPackProvider());
		/*? } else {*//*
        vanillaResourcePackProviders.add(new VanillaDataPackProvider(MinecraftClient.getInstance().getSymlinkFinder()));
        *//*? }*/

		return vanillaResourcePackProviders;
	}

	@ExpectPlatform
	public static ArrayList<ResourcePackProvider> getPlatformResourcePackProviders() {
		throw new AssertionError();
	}

	public static ArrayList<ResourcePackProvider> getModsResourcePackProviders() {
		ArrayList<ResourcePackProvider> modResourcePackProviders = new ArrayList<>();

		for (ModCompat compat : ModChecker.CUSTOM_RESOURCE_PACK_PROVIDER_COMPATS) {
			var resourcePackProviders = compat.getResourcePackProviders();
			modResourcePackProviders.addAll(resourcePackProviders);
		}

		return modResourcePackProviders;
	}
}
