package com.faboslav.structurify.common.platform;

import net.minecraft.server.packs.repository.RepositorySource;

import java.util.ArrayList;

public interface PlatformResourcePackProvider
{
	ArrayList<RepositorySource> getPlatformResourcePackProviders();
}
