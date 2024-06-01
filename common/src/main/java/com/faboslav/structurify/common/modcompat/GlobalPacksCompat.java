package com.faboslav.structurify.common.modcompat;

import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourceType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;

public final class GlobalPacksCompat implements ModCompat
{
	@Override
	public EnumSet<Type> compatTypes() {
		return EnumSet.of(Type.CUSTOM_RESOURCE_PACK_PROVIDERS);
	}

	@Override
	public ArrayList<ResourcePackProvider> getResourcePackProviders() {
		var resourcePackProviders = new ArrayList<ResourcePackProvider>();

		try {
			Class<?> commonClass = Class.forName("net.dark_roleplay.gdarp.CommonClass");
			Method getRepositorySourceMethod = commonClass.getMethod("getRepositorySource", ResourceType.class, boolean.class);

			resourcePackProviders.add((ResourcePackProvider) getRepositorySourceMethod.invoke(null, ResourceType.SERVER_DATA, true));
			resourcePackProviders.add((ResourcePackProvider) getRepositorySourceMethod.invoke(null, ResourceType.SERVER_DATA, false));
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
				 java.lang.reflect.InvocationTargetException e) {
			System.out.println("Dependency not found or method invocation failed: " + e.getMessage());
		}

		return resourcePackProviders;
	}
}
