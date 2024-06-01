package com.faboslav.structurify.common.modcompat.fabric;

import com.faboslav.structurify.common.modcompat.ModCompat;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;

public final class GlobalDatapacksCompat implements ModCompat
{
	@Override
	public EnumSet<Type> compatTypes() {
		return EnumSet.of(Type.CUSTOM_RESOURCE_PACK_PROVIDERS);
	}

	@Override
	public ArrayList<ResourcePackProvider> getResourcePackProviders() {
		var resourcePackProviders = new ArrayList<ResourcePackProvider>();

		try {
			Class<?> globalDatapackClass = Class.forName("me.declipsonator.globaldatapack.GlobalDatapack");
			Field globalPackFolderField = globalDatapackClass.getField("globalPackFolder");
			Path globalPackFolder = (Path) globalPackFolderField.get(null);

			resourcePackProviders.add(new FileResourcePackProvider(globalPackFolder, ResourceType.SERVER_DATA, ResourcePackSource.WORLD));
		} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
			System.out.println("Dependency not found or field access failed: " + e.getMessage());
		}

		return resourcePackProviders;
	}
}
