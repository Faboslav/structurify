package com.faboslav.structurify.common.modcompat.fabric;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.modcompat.ModCompat;
import com.yungnickyoung.minecraft.paxi.PaxiCommon;
import com.yungnickyoung.minecraft.paxi.PaxiFabric;
import com.yungnickyoung.minecraft.paxi.PaxiPackSource;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;

import java.util.ArrayList;
import java.util.EnumSet;

public final class PaxiCompat implements ModCompat
{
	@Override
	public EnumSet<Type> compatTypes() {
		return EnumSet.of(Type.CUSTOM_RESOURCE_PACK_PROVIDERS);
	}

	@Override
	public ArrayList<ResourcePackProvider> getResourcePackProviders() {
		var resourcePackProviders = new ArrayList<ResourcePackProvider>();

		// This is just a reminder for me, that paxi handles everything correctly and works out of the box in all mod loaders, so no additional work needed
		// Structurify.getLogger().info(String.valueOf(PaxiCommon.DATA_PACK_DIRECTORY));
		// resourcePackProviders.add(new FileResourcePackProvider(PaxiCommon.DATA_PACK_DIRECTORY, ResourceType.SERVER_DATA, PaxiPackSource.PACK_SOURCE_PAXI));

		return resourcePackProviders;
	}
}
