package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.commands.Commands;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.Nullable;
import java.util.*;

//? if >= 1.21.11 {
import net.minecraft.server.permissions.PermissionSet;
//?}

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

//? if <= 1.21.1 {
/*import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
*///?}

public final class StructurifyRegistryManagerProvider
{
	@Nullable
	private static HolderLookup.Provider registryManager = null;
	private static boolean isLoading = false;
	private static final Map<ResourceKey<? extends Registry<?>>, Codec<?>> REGISTRIES = Map.ofEntries(
		Map.entry(Registries.BIOME, Biome.DIRECT_CODEC),
		Map.entry(Registries.CONFIGURED_CARVER, ConfiguredWorldCarver.DIRECT_CODEC),
		Map.entry(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC),
		Map.entry(Registries.TEMPLATE_POOL, StructureTemplatePool.DIRECT_CODEC),
		Map.entry(Registries.CONFIGURED_FEATURE, ConfiguredFeature.DIRECT_CODEC),
		Map.entry(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC),
		Map.entry(Registries.STRUCTURE, Structure.DIRECT_CODEC),
		Map.entry(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC)

	);

	@Nullable
	public static HolderLookup.Provider getRegistryManager() {
		if (registryManager == null) {
			loadRegistryManager();
		}

		return registryManager;
	}

	@Nullable
	public static DynamicOps<JsonElement> getSerializationContext()
	{
		var registryManager = getRegistryManager();

		if(registryManager == null) {
			return null;
		}

		//? if >= 1.21 {
		return registryManager.createSerializationContext(JsonOps.INSTANCE);
		//?} else {
		/*return RegistryOps.create(JsonOps.INSTANCE, registryManager);
		*///?}
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

	@Nullable
	public static HolderLookup.RegistryLookup<StructureSet> getStructureSetRegistry() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return null;
		}

		return registryManager.lookup(Registries.STRUCTURE_SET).orElse(null);
	}

	@Nullable
	public static HolderLookup.RegistryLookup<StructureTemplatePool> getStructureTemplatePoolRegistry() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return null;
		}

		return registryManager.lookup(Registries.TEMPLATE_POOL).orElse(null);
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
			var resourcePackManager = StructurifyResourcePackProvider.getResourcePackRepository();

			var dataPacks = new WorldLoader.PackConfig(resourcePackManager, WorldDataConfiguration.DEFAULT, false, false);
			var serverConfig = new WorldLoader.InitConfig(dataPacks, Commands.CommandSelection.INTEGRATED, /*? if >= 1.21.11 {*/PermissionSet.ALL_PERMISSIONS/*?} else {*//*2*//*?}*/);

			var saveLoader = Util.blockUntilDone(executor ->
				WorldLoader.load(serverConfig, loadContextSupplierContext -> {
					var registry = new MappedRegistry<>(Registries.LEVEL_STEM, Lifecycle.stable()).freeze();

					//? if >=1.21.3 {
					var dimensionsConfig = loadContextSupplierContext
						.datapackWorldgen()
						.lookupOrThrow(Registries.WORLD_PRESET)
						.getOrThrow(WorldPresets.FLAT)
						.value()
						.createWorldDimensions()
						.bake(registry);
					//?} else {
					/*var dimensionsConfig = loadContextSupplierContext
						.datapackWorldgen()
						.registryOrThrow(Registries.WORLD_PRESET)
						.getHolderOrThrow(WorldPresets.FLAT)
						.value()
						.createWorldDimensions()
						.bake(registry);
					*///?}

					return new WorldLoader.DataLoadOutput<>(null, dimensionsConfig.dimensionsRegistryAccess());
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

	public static void loadRegistryManagerNew() {
		if (isLoading) {
			return;
		}

		isLoading = true;

		try {
			Structurify.getLogger().info("Loading registry manager...");
			var resourcePackManager = StructurifyResourcePackProvider.getResourcePackRepository();

			try (var resourceManager = new MultiPackResourceManager(
				PackType.SERVER_DATA,
				resourcePackManager.openAllSelected()
			)) {
				List<RegistryDataLoader.RegistryData<?>> registries = RegistryDataLoader.WORLDGEN_REGISTRIES;
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

				//? if <= 1.21.1 {
				/*bindRegistryTags(resourceManager, registryAccess, Registries.BIOME);
				bindRegistryTags(resourceManager, registryAccess, Registries.CONFIGURED_CARVER);
				bindRegistryTags(resourceManager, registryAccess, Registries.PROCESSOR_LIST);
				bindRegistryTags(resourceManager, registryAccess, Registries.TEMPLATE_POOL);
				bindRegistryTags(resourceManager, registryAccess, Registries.CONFIGURED_FEATURE);
				bindRegistryTags(resourceManager, registryAccess, Registries.PLACED_FEATURE);
				bindRegistryTags(resourceManager, registryAccess, Registries.STRUCTURE);
				bindRegistryTags(resourceManager, registryAccess, Registries.STRUCTURE_SET);
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

	//? if <= 1.21.1 {
	/*private static <T> void bindRegistryTags(
		ResourceManager resourceManager,
		RegistryAccess registryAccess,
		ResourceKey<? extends Registry<T>> registryKey
	) {
		Registry<T> registry = registryAccess.registryOrThrow(registryKey);
		String tagsDirectory =
			//? if >= 1.21 {
			Registries.tagsDirPath(registryKey)
			//?} else {
			/^"tags/" + registryKey.location().getPath()
			^///?}

			;
		TagLoader<Holder<T>> loader = new TagLoader<>(id -> registry.getHolder(ResourceKey.create(registryKey, id)), tagsDirectory);
		Map<Identifier, Collection<Holder<T>>> loadedTags = loader.loadAndBuild(resourceManager);
		Map<TagKey<T>, List<Holder<T>>> tags = new HashMap<>();
		loadedTags.forEach((id, holders) -> tags.put(TagKey.create(registryKey, id), List.copyOf(holders)));
		registry.bindTags(tags);
	}
	*///?}
}