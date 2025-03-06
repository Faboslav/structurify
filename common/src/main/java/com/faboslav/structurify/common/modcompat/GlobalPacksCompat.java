package com.faboslav.structurify.common.modcompat;

//? global_packs: >0 {
import net.dark_roleplay.gdarp.CommonClass;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;
import java.util.EnumSet;

public final class GlobalPacksCompat implements ModCompat
{
	@Override
	public EnumSet<Type> compatTypes() {
		return EnumSet.of(Type.CUSTOM_RESOURCE_PACK_PROVIDERS);
	}

	@Override
	public ArrayList<RepositorySource> getResourcePackProviders() {
		var resourcePackProviders = new ArrayList<RepositorySource>();

		resourcePackProviders.add(CommonClass.getRepositorySource(PackType.SERVER_DATA, true));
		resourcePackProviders.add(CommonClass.getRepositorySource(PackType.SERVER_DATA, false));

		return resourcePackProviders;
	}
}
//?}
