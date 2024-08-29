package com.faboslav.structurify.common.registry.fabric;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;

public final class StructurifyResourcePackProviderImpl
{
	public static ArrayList<RepositorySource> getPlatformResourcePackProviders() {
		ArrayList<RepositorySource> platformResourcePackProviders = new ArrayList<>();

		platformResourcePackProviders.add(new ModResourcePackCreator(PackType.SERVER_DATA));

		return platformResourcePackProviders;
	}
}
