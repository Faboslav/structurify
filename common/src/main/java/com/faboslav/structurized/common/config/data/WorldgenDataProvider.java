package com.faboslav.structurized.common.config.data;

import com.faboslav.structurized.common.Structurized;
import com.faboslav.structurized.common.api.StructurizedRandomSpreadStructurePlacement;
import com.faboslav.structurized.common.mixin.CreateWorldScreenAccessor;
import com.mojang.serialization.Lifecycle;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.registry.*;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.SaveLoading;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class WorldgenDataProvider
{
	@Nullable
	private static DynamicRegistryManager.Immutable registryManager = null;

	@Nullable
	public static DynamicRegistryManager.Immutable getRegistryManager() {
		if(registryManager == null) {
			var possibleRegistryManager = tryToGetRegistryManager();

			if(possibleRegistryManager != null) {
				registryManager = possibleRegistryManager;
			}
		}

		return registryManager;
	}

	@ExpectPlatform
	@Nullable
	public static DynamicRegistryManager.Immutable tryToGetRegistryManager() {
		throw new NotImplementedException();
	}

	public static Map<String, StructureData> getStructures() {
		var registryManager = getRegistryManager();

		if(registryManager == null) {
			return Collections.emptyMap();
		}

		var structureRegistry = registryManager.get(RegistryKeys.STRUCTURE);
		var biomeRegistry = registryManager.get(RegistryKeys.BIOME);
		Map<String, StructureData> structures = new TreeMap<>();

		for (Structure structure : structureRegistry) {
			RegistryKey<Structure> structureRegistryKey = structureRegistry.getKey(structure).orElse(null);

			if (structureRegistryKey == null) {
				continue;
			}

			String structureId = structureRegistryKey.getValue().toString();
			var biomeStorage = structure.getValidBiomes().getStorage();
			Set<String> defaultBiomes = new HashSet<>();

			biomeStorage.mapLeft(biomeTagKey -> {
				biomeRegistry.getEntryList(biomeTagKey).ifPresent(biomes -> {
					for (var biome : biomes) {
						String biomeKey = biome.getKey().get().getValue().toString();
						defaultBiomes.add(biomeKey);
					}
				});

				return null;
			});

			structures.put(
				structureId,
				new StructureData(
					false,
					defaultBiomes,
					new HashSet<>()
				)
			);
		}

		return structures;
	}

	public static Map<String, StructureSetData> getStructureSets() {
		var registryManager = getRegistryManager();

		if(registryManager == null) {
			return Collections.emptyMap();
		}

		var structureSetRegistry = registryManager.get(RegistryKeys.STRUCTURE_SET);
		Map<String, StructureSetData> structureSets = new TreeMap<>();

		for (StructureSet structureSet : structureSetRegistry) {
			RegistryKey<StructureSet> structureSetRegistryKey = structureSetRegistry.getKey(structureSet).orElse(null);

			if (structureSetRegistryKey == null) {
				continue;
			}

			Identifier structureSetId = structureSetRegistryKey.getValue();
			String structureSetStringId = structureSetId.toString();

			if (structureSet.placement() instanceof RandomSpreadStructurePlacement) {
				RandomSpreadStructurePlacement randomSpreadStructurePlacement = (RandomSpreadStructurePlacement) structureSet.placement();
				((StructurizedRandomSpreadStructurePlacement)randomSpreadStructurePlacement).structurized$setStructureIdentifier(structureSetId);
				structureSets.put(structureSetStringId, new StructureSetData(randomSpreadStructurePlacement.getSpacing(), randomSpreadStructurePlacement.getSeparation()));
			} else {
				structureSets.put(structureSetStringId, new StructureSetData(-1, -1));
			}
		}

		return structureSets;
	}
}
