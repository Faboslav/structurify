package com.faboslav.structurify.common.modcompat;

//? open_loader {
/*import com.faboslav.structurify.common.util.Platform;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;
import java.util.EnumSet;

//? <=1.20.1 {
/^import net.darkhax.openloader.config.ConfigSchema;
import net.darkhax.openloader.packs.OpenLoaderRepositorySource;
import net.darkhax.openloader.packs.RepoType;
^///?} else {
import net.darkhax.openloader.common.impl.packs.OpenLoaderRepositorySource;
import net.minecraft.server.packs.PackType;
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
		/^var configDir = Platform.getConfigDirectory().resolve("openloader");
		var config = ConfigSchema.load(configDir);

		resourcePackProviders.add(new OpenLoaderRepositorySource(RepoType.DATA, config, config.dataPacks, configDir));
		^///?} else {
		resourcePackProviders.add(new OpenLoaderRepositorySource(PackType.SERVER_DATA));
		//?}

		return resourcePackProviders;
	}
}
*///?}
