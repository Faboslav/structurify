package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.mojang.serialization.Codec;
import net.minecraft.core.*;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.Nullable;
import java.util.List;

//? if >= 1.21.3 {
import net.minecraft.server.RegistryLayer;
import net.minecraft.tags.TagLoader;
//?} else {
/*import net.minecraft.core.registries.BuiltInRegistries;
*///?}

//? if >= 26.1 {
import net.minecraft.resources.RegistryValidator;
import net.minecraft.util.Util;
//?}

public final class StructurifyRegistryManagerProvider
{
	@Nullable
	private static HolderLookup.Provider registryManager = null;
	private static boolean isLoading = false;

	@Nullable
	public static HolderLookup.Provider getRegistryManager() {
		if (registryManager == null) {
			loadRegistryManager();
		}

		return registryManager;
	}

	@Nullable
	public static HolderLookup.RegistryLookup<Biome> getBiomeRegistry() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return null;
		}

		return registryManager.lookup(Registries.BIOME).orElse(null);
	}

	@Nullable
	public static HolderLookup.RegistryLookup<Structure> getStructureRegistry() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return null;
		}

		return registryManager.lookup(Registries.STRUCTURE).orElse(null);
	}

	public static void setRegistryManager(HolderLookup.Provider registryAccess) {
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
			resourcePackManager.reload();
			resourcePackManager.setSelected(resourcePackManager.getAvailableIds());

			try (var resourceManager = new MultiPackResourceManager(
				PackType.SERVER_DATA,
				resourcePackManager.openAllSelected()
			)) {
				List<RegistryDataLoader.RegistryData<?>> registries = List.of(
					getRegistryDataLoader(Registries.BIOME, Biome.DIRECT_CODEC),
					getRegistryDataLoader(Registries.CONFIGURED_CARVER, ConfiguredWorldCarver.DIRECT_CODEC),
					getRegistryDataLoader(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC),
					getRegistryDataLoader(Registries.TEMPLATE_POOL, StructureTemplatePool.DIRECT_CODEC),
					getRegistryDataLoader(Registries.CONFIGURED_FEATURE, ConfiguredFeature.DIRECT_CODEC),
					getRegistryDataLoader(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC),
					getRegistryDataLoader(Registries.STRUCTURE, Structure.DIRECT_CODEC),
					getRegistryDataLoader(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC)
				);
				//? if >= 1.21.3 {
				LayeredRegistryAccess<RegistryLayer> initialLayers = RegistryLayer.createRegistryAccess();
				List<Registry.PendingTags<?>> staticLayerTags = TagLoader.loadTagsForExistingRegistries(
					resourceManager,
					initialLayers.getLayer(RegistryLayer.STATIC)
				);
				var baseRegistryAccess = TagLoader.buildUpdatedLookups(
					initialLayers.getAccessForLoading(RegistryLayer.WORLDGEN),
					staticLayerTags
				);
				//?} else {
				/*var baseRegistryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
				*///?}
				//? if >= 26.1 {
				var registryAccess = Util.blockUntilDone(executor ->
					RegistryDataLoader.load(
						resourceManager,
						baseRegistryAccess,
						registries,
						executor
					)
				).get();
				//?} else {
				/*var registryAccess = RegistryDataLoader.load(
					resourceManager,
					baseRegistryAccess,
					registries
				);
				*///?}

				setRegistryManager(registryAccess);
			}

			Structurify.getLogger().info("Finished loading registry manager");
		} catch (Exception exception) {
			Structurify.getLogger().error("Failed to load registry manager.", exception);
		} finally {
			isLoading = false;
		}
	}

	private static <T> RegistryDataLoader.RegistryData<T> getRegistryDataLoader(ResourceKey<? extends Registry<T>> key, Codec<T> codec) {
		//? if >= 26.1 {
		return new RegistryDataLoader.RegistryData<>(key, codec, RegistryValidator.none());
		//?} else if >= 1.21.1 {
		/*return new RegistryDataLoader.RegistryData<>(key, codec, false);
		*///?} else {
		/*return new RegistryDataLoader.RegistryData<>(key, codec);
		*///?}
	}
}
