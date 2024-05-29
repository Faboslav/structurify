package com.faboslav.structurized.common.config.data.fabric;

import com.faboslav.structurized.common.Structurized;
import com.faboslav.structurized.common.mixin.CreateWorldScreenAccessor;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.resource.*;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.SaveLoading;
import net.minecraft.util.Util;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.Nullable;

public final class WorldgenDataProviderImpl
{
	@Nullable
	public static DynamicRegistryManager.Immutable tryToGetRegistryManager() {
		try {
			ResourcePackManager packRepository = new ResourcePackManager(new VanillaDataPackProvider(), new ModResourcePackCreator(ResourceType.SERVER_DATA));
			SaveLoading.ServerConfig serverConfig = CreateWorldScreenAccessor.callCreateServerConfig(packRepository, DataConfiguration.SAFE_MODE);
			SaveLoader saveLoader = Util.waitAndApply(executor -> SaveLoading.load(serverConfig, loadContextSupplierContext -> {
				Registry<DimensionOptions> registry = new SimpleRegistry<>(RegistryKeys.DIMENSION, Lifecycle.stable()).freeze();
				DimensionOptionsRegistryHolder.DimensionsConfig dimensionsConfig = loadContextSupplierContext.worldGenRegistryManager().get(RegistryKeys.WORLD_PRESET).entryOf(WorldPresets.FLAT).value().createDimensionsRegistryHolder().toConfig(registry);
				return new SaveLoading.LoadContext<LevelProperties>(null, dimensionsConfig.toDynamicRegistryManager());
			}, SaveLoader::new, Util.getMainWorkerExecutor(), executor)).get();

			return saveLoader.combinedDynamicRegistries().getCombinedRegistryManager();
		} catch (Exception exception) {
			Structurized.getLogger().error("Failed to load registry manager.", exception);
			return null;
		}
	}
}
