package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Util;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.Nullable;

public final class StructurifyRegistryManagerProvider
{
	@Nullable
	private static DynamicRegistryManager.Immutable registryManager = null;
	private static boolean isLoading = false;

	public static DynamicRegistryManager.@Nullable Immutable getRegistryManager() {
		return registryManager;
	}

	public static void reloadRegistryManager() {
		if (isLoading) {
			return;
		}

		isLoading = true;
		try {
			Structurify.getLogger().info("Loading registry manager...");
			var resourcePackProviders = StructurifyResourcePackProvider.getResourcePackProviders();

			for (var resourcePackProvider : resourcePackProviders) {
				Structurify.getLogger().info("Registering resource pack provider: " + resourcePackProvider.getClass().getSimpleName());
			}

			ResourcePackManager resourcePackManager = new ResourcePackManager(StructurifyResourcePackProvider.getResourcePackProviders().toArray(new ResourcePackProvider[0]));
			SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, DataConfiguration.SAFE_MODE, false, false);
			SaveLoading.ServerConfig serverConfig = new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);

			SaveLoader saveLoader = Util.waitAndApply(executor ->
				SaveLoading.load(serverConfig, loadContextSupplierContext -> {
					Registry<DimensionOptions> registry = new SimpleRegistry<>(RegistryKeys.DIMENSION, Lifecycle.stable()).freeze();
					DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = loadContextSupplierContext.worldGenRegistryManager().get(RegistryKeys.WORLD_PRESET).entryOf(WorldPresets.FLAT).value().createDimensionsRegistryHolder().toConfig(registry);
					return new SaveLoading.LoadContext<LevelProperties>(null, dimensionsConfig.toDynamicRegistryManager());
				}, SaveLoader::new, Util.getMainWorkerExecutor(), executor)
			).get();

			if (saveLoader == null || saveLoader.combinedDynamicRegistries() == null) {
				Structurify.getLogger().error("SaveLoader or CombinedDynamicRegistries is null.");
				return;
			}

			registryManager = saveLoader.combinedDynamicRegistries().getCombinedRegistryManager();
			Structurify.getLogger().info("Finished loading registry manager");
		} catch (Exception exception) {
			Structurify.getLogger().error("Failed to load registry manager.", exception);
		} finally {
			isLoading = false;
		}
	}
}
