package com.faboslav.structurify.fabric.platform;

import com.faboslav.structurify.common.platform.PlatformResourcePackProvider;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;

//? if >= 1.21.11 {
import net.fabricmc.fabric.impl.resource.pack.ModResourcePackCreator;
//?} else {
/*import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
*///?}

public class FabricPlatformResourcePackProvider implements PlatformResourcePackProvider
{
	public ArrayList<RepositorySource> getPlatformResourcePackProviders() {
		ArrayList<RepositorySource> platformResourcePackProviders = new ArrayList<>();
		platformResourcePackProviders.add(new ModResourcePackCreator(PackType.SERVER_DATA));

		return platformResourcePackProviders;
	}
}
