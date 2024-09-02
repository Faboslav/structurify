package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.mojang.serialization.Lifecycle;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.jetbrains.annotations.Nullable;

public final class StructurifyRegistryManagerProvider
{
	@Nullable
	private static RegistryAccess registryManager = null;
	private static boolean isLoading = false;

	@Nullable
	public static RegistryAccess getRegistryManager() {
		if(registryManager == null) {
			loadRegistryManager();
		}

		return registryManager;
	}

	public static void setRegistryManager(RegistryAccess registryAccess) {
		registryManager = registryAccess;
	}

	public static void loadRegistryManager() {
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

			var resourcePackManager = new PackRepository(StructurifyResourcePackProvider.getResourcePackProviders().toArray(new RepositorySource[0]));
			var dataPacks = new WorldLoader.PackConfig(resourcePackManager, WorldDataConfiguration.DEFAULT, false, false);
			var serverConfig = new WorldLoader.InitConfig(dataPacks, Commands.CommandSelection.INTEGRATED, 2);

			var saveLoader = Util.blockUntilDone(executor ->
				WorldLoader.load(serverConfig, loadContextSupplierContext -> {
					var registry = new MappedRegistry<>(Registries.LEVEL_STEM, Lifecycle.stable()).freeze();

					var dimensionsConfig = loadContextSupplierContext
						.datapackWorldgen()
						.registryOrThrow(Registries.WORLD_PRESET)
						.getHolderOrThrow(WorldPresets.FLAT)
						.value()
						.createWorldDimensions()
						.bake(registry);

					return new WorldLoader.DataLoadOutput<PrimaryLevelData>(null, dimensionsConfig.dimensionsRegistryAccess());
				}, WorldStem::new, Util.backgroundExecutor(), executor)
			).get();

			if (saveLoader == null || saveLoader.registries() == null) {
				Structurify.getLogger().error("SaveLoader or CombinedDynamicRegistries is null.");
				return;
			}

			setRegistryManager(saveLoader.registries().compositeAccess());
			Structurify.getLogger().info("Finished loading registry manager");
		} catch (Exception exception) {
			Structurify.getLogger().error("Failed to load registry manager.", exception);
		} finally {
			isLoading = false;
		}
	}
}
