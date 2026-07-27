package com.faboslav.structurify.common.platform;

import com.faboslav.structurify.common.network.platform.NetworkPlatform;

import java.util.ServiceLoader;

public final class PlatformHooks
{
	public static final PlatformCompat PLATFORM_COMPAT = load(PlatformCompat.class);
	public static final PlatformHelper PLATFORM_HELPER = load(PlatformHelper.class);
	public static final NetworkPlatform NETWORK_PLATFORM = load(NetworkPlatform.class);
	public static final PlatformResourcePackProvider PLATFORM_RESOURCE_PACK_PROVIDER = load(PlatformResourcePackProvider.class);

	public static <T> T load(Class<T> service) {
		T loadedService = ServiceLoader.load(service)
			.findFirst()
			.orElseThrow(() -> new NullPointerException("No implementation found for " + service.getName()));
		return loadedService;
	}
}
