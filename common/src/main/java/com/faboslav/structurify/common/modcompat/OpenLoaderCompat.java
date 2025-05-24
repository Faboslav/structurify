package com.faboslav.structurify.common.modcompat;


//? open_loader {
/*import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;
import java.util.EnumSet;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

//? <=1.20.1 {
/^import net.darkhax.openloader.config.ConfigSchema;
import net.darkhax.openloader.packs.OpenLoaderRepositorySource;
import net.darkhax.openloader.packs.RepoType;
import com.faboslav.structurify.common.platform.PlatformHooks;
^///?} else {
import net.darkhax.openloader.common.impl.packs.OpenLoaderRepositorySource;
import net.minecraft.server.packs.PackType;
import java.util.LinkedList;
import java.io.File;
import net.darkhax.openloader.common.impl.Platform;
import net.darkhax.openloader.common.impl.OpenLoader;
//?}

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
		/^var configDir = PlatformHooks.PLATFORM_HELPER.getConfigDirectory().resolve("openloader");
		^///?} else {
		//?}

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
*///?}

//var config = ConfigSchema.load(configDir);
//resourcePackProviders.add(new OpenLoaderRepositorySource(RepoType.DATA, config, config.dataPacks, configDir));
//resourcePackProviders.add(OpenLoader.DATA_SOURCE.get());