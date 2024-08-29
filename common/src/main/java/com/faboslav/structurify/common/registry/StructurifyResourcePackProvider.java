package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.modcompat.ModChecker;
import com.faboslav.structurify.common.modcompat.ModCompat;
import dev.architectury.injectables.annotations.ExpectPlatform;
import java.util.ArrayList;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;

/*? if >=1.20.2 {*/
/*import net.minecraft.world.level.validation.DirectoryValidator;
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

		/*? if =1.20.1 {*/
		vanillaResourcePackProviders.add(new ServerPacksSource());
		/*?} else if >=1.20.2 {*/
		/*vanillaResourcePackProviders.add(new ServerPacksSource(new DirectoryValidator(path -> true)));
		 *//*?}*/

		return vanillaResourcePackProviders;
	}

	@ExpectPlatform
	public static ArrayList<RepositorySource> getPlatformResourcePackProviders() {
		throw new AssertionError();
	}

	public static ArrayList<RepositorySource> getModsResourcePackProviders() {
		ArrayList<RepositorySource> modResourcePackProviders = new ArrayList<>();

		for (ModCompat compat : ModChecker.CUSTOM_RESOURCE_PACK_PROVIDER_COMPATS) {
			Structurify.getLogger().info("Loading: " + compat.toString());
			var resourcePackProviders = compat.getResourcePackProviders();
			modResourcePackProviders.addAll(resourcePackProviders);
		}

		return modResourcePackProviders;
	}
}
