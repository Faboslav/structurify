package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.modcompat.ModChecker;
import com.faboslav.structurify.common.modcompat.ModCompat;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;

import java.util.ArrayList;

/*? if >=1.21 {*/
/*import com.faboslav.structurify.common.mixin.ResourcePackManagerAccessor;
*//*?}*/

public final class StructurifyResourcePackProvider
{
	public static ArrayList<RepositorySource> getResourcePackProviders() {
		ArrayList<RepositorySource> resourcePackProviders = new ArrayList<>();

		resourcePackProviders.addAll(getVanillaResourcePackProviders());
		resourcePackProviders.addAll(getPlatformResourcePackProviders());
		resourcePackProviders.addAll(getModsResourcePackProviders());

		return resourcePackProviders;
	}

	public static ArrayList<RepositorySource> getVanillaResourcePackProviders() {
		ArrayList<RepositorySource> vanillaResourcePackProviders = new ArrayList<>();

		/*? if >=1.21 {*/
		/*vanillaResourcePackProviders.addAll(((ResourcePackManagerAccessor)ServerPacksSource.createVanillaTrustedRepository()).getSources());
		 *//*?} else {*/
		vanillaResourcePackProviders.add(new ServerPacksSource());
		/*?}*/

		return vanillaResourcePackProviders;
	}

	@ExpectPlatform
	public static ArrayList<RepositorySource> getPlatformResourcePackProviders() {
		throw new AssertionError();
	}

	public static ArrayList<RepositorySource> getModsResourcePackProviders() {
		ArrayList<RepositorySource> modResourcePackProviders = new ArrayList<>();

		for (ModCompat compat : ModChecker.CUSTOM_RESOURCE_PACK_PROVIDER_COMPATS) {
			var resourcePackProviders = compat.getResourcePackProviders();
			modResourcePackProviders.addAll(resourcePackProviders);
		}

		return modResourcePackProviders;
	}
}
