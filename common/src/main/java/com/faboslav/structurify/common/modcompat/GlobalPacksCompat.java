package com.faboslav.structurify.common.modcompat;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

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
	public ArrayList<RepositorySource> getResourcePackProviders() {
		var resourcePackProviders = new ArrayList<RepositorySource>();

		try {
			Class<?> commonClass = Class.forName("net.dark_roleplay.gdarp.CommonClass");
			Method getRepositorySourceMethod = commonClass.getMethod("getRepositorySource", PackType.class, boolean.class);

			resourcePackProviders.add((RepositorySource) getRepositorySourceMethod.invoke(null, PackType.SERVER_DATA, true));
			resourcePackProviders.add((RepositorySource) getRepositorySourceMethod.invoke(null, PackType.SERVER_DATA, false));
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
				 java.lang.reflect.InvocationTargetException e) {
			System.out.println("Dependency not found or method invocation failed: " + e.getMessage());
		}

		return resourcePackProviders;
	}
}
