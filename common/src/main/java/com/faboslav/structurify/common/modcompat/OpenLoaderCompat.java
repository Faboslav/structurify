package com.faboslav.structurify.common.modcompat;

//? open_loader {
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;
import java.util.EnumSet;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

//? <=1.20.1 {
import net.darkhax.openloader.config.ConfigSchema;
import net.darkhax.openloader.packs.OpenLoaderRepositorySource;
import net.darkhax.openloader.packs.RepoType;
import com.faboslav.structurify.common.platform.PlatformHooks;
//?} else {
/*import net.darkhax.openloader.common.impl.packs.OpenLoaderRepositorySource;
import net.minecraft.server.packs.PackType;
import java.util.LinkedList;
import java.io.File;
import net.darkhax.openloader.common.impl.Platform;
import net.darkhax.openloader.common.impl.OpenLoader;
*///?}

public final class OpenLoaderCompat implements ModCompat
{
	@Override
	public EnumSet<Type> compatTypes() {
		return EnumSet.of(Type.CUSTOM_RESOURCE_PACK_PROVIDERS);
	}

	@Override
	public ArrayList<RepositorySource> getResourcePackProviders() {
		var resourcePackProviders = new ArrayList<RepositorySource>();

		//? <=1.20.1 {
		var configDir = PlatformHooks.PLATFORM_HELPER.getConfigDirectory().resolve("openloader");
		var config = ConfigSchema.load(configDir);

		resourcePackProviders.add(new OpenLoaderRepositorySource(RepoType.DATA, config, config.dataPacks, configDir));
		//?} else {

		/*// Copy & Pasted solution
		// https://github.com/Darkhax-Minecraft/Open-Loader/issues/44
		// resourcePackProviders.add(OpenLoader.DATA_SOURCE.get());
		var scanLocations = new LinkedList<>();
		if (OpenLoader.CONFIG.get().load_data_packs) {
			final File packFolder = new File(Platform.PLATFORM.getConfigDirectory(), "openloader/packs");
			if (packFolder.mkdirs()) {
				OpenLoader.LOG.info("Created packs folder a '{}'", packFolder.getAbsolutePath());
			}
			final File datapacksDir = new File(Platform.PLATFORM.getGameDirectory(), "datapacks");
			if (datapacksDir.exists() && OpenLoader.CONFIG.get().load_datapacks_dir) {
				scanLocations.add(datapacksDir);
			}
			scanLocations.add(packFolder);
			for (String location : OpenLoader.CONFIG.get().additional_locations) {
				if (isValidPath(location)) {
					final File file = new File(location);
					if (file.exists()) {
						scanLocations.add(file);
					}
				}
			}
		}
		*///?}

		return resourcePackProviders;
	}

	private static boolean isValidPath(String path) {
		try {
			Paths.get(path);
		} catch (InvalidPathException | NullPointerException ex) {
			return false;
		}
		return true;
	}
}
//?}
