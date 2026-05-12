package com.faboslav.structurify.common.platform;

import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;

public interface PlatformResourcePackProvider
{
	default ArrayList<RepositorySource> getPlatformResourcePackProviders() {
		return new ArrayList<>();
	}
	default void loadPlatformResourcePacks(PackRepository resourcePackManager) {}
}
