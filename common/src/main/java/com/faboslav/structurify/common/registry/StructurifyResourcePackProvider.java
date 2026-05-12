package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.modcompat.ModChecker;
import com.faboslav.structurify.common.modcompat.ModCompat;
import com.faboslav.structurify.common.platform.PlatformHooks;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;

import java.util.ArrayList;

//? if >=1.21 {
import com.faboslav.structurify.common.mixin.ResourcePackManagerAccessor;
 //?}

public final class StructurifyResourcePackProvider
{
	public static ArrayList<RepositorySource> getResourcePackProviders() {
		ArrayList<RepositorySource> resourcePackProviders = new ArrayList<>();

		resourcePackProviders.addAll(getVanillaResourcePackProviders());
		resourcePackProviders.addAll(PlatformHooks.PLATFORM_RESOURCE_PACK_PROVIDER.getPlatformResourcePackProviders());
		resourcePackProviders.addAll(getModsResourcePackProviders());

		return resourcePackProviders;
	}

	public static ArrayList<RepositorySource> getVanillaResourcePackProviders() {
		ArrayList<RepositorySource> vanillaResourcePackProviders = new ArrayList<>();

		//? if >=1.21 {
		vanillaResourcePackProviders.addAll(((ResourcePackManagerAccessor) ServerPacksSource.createVanillaTrustedRepository()).getSources());
		 //?} else {
		/*vanillaResourcePackProviders.add(new ServerPacksSource());
		*///?}

		return vanillaResourcePackProviders;
	}

	public static ArrayList<RepositorySource> getModsResourcePackProviders() {
		ArrayList<RepositorySource> modResourcePackProviders = new ArrayList<>();

		for (ModCompat compat : ModChecker.CUSTOM_RESOURCE_PACK_PROVIDER_COMPATS) {
			try {
				var resourcePackProviders = compat.getResourcePackProviders();
				modResourcePackProviders.addAll(resourcePackProviders);
			} catch (Throwable e) {
				Structurify.getLogger().error("Failed to get resource pack providers from mod compat");
				e.printStackTrace();
			}
		}

		return modResourcePackProviders;
	}

	public static PackRepository getResourcePackRepository() {
		Structurify.getLogger().info("Loading resource pack repository...");
		var resourcePackProviders = StructurifyResourcePackProvider.getResourcePackProviders();

		for (var resourcePackProvider : resourcePackProviders) {
			Structurify.getLogger().info("Loaded resource pack provider: " + resourcePackProvider.getClass().getSimpleName());
		}

		var resourcePackManager = new PackRepository(StructurifyResourcePackProvider.getResourcePackProviders().toArray(new RepositorySource[0]));
		PlatformHooks.PLATFORM_RESOURCE_PACK_PROVIDER.loadPlatformResourcePacks(resourcePackManager);
		resourcePackManager.reload();
		resourcePackManager.setSelected(resourcePackManager.getAvailableIds());
		Structurify.getLogger().info("Finished loading resource pack repository");

		return resourcePackManager;
	}
}
