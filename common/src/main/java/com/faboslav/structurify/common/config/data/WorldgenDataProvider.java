package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.*;
import com.faboslav.structurify.common.config.data.structure.JigsawData;
import com.faboslav.structurify.common.config.data.structure.jigsaw.HeightProviderData;
import com.faboslav.structurify.common.config.data.structure.jigsaw.ProjectStartToHeightmap;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.faboslav.structurify.common.registry.StructurifyTemplatePoolProvider;
import com.faboslav.structurify.common.util.BiomeUtil;
import com.faboslav.structurify.common.util.JigsawStructureUtil;
import com.faboslav.structurify.common.util.StructureUtil;
import com.faboslav.structurify.common.util.StructurifyComparators;
import com.google.gson.JsonObject;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.*;

//? if yungs_api || repurposed_structures {
/*import com.faboslav.structurify.common.platform.PlatformHooks;
*///?}

//? if yungs_api {
/*import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
 *///?}

//? if repurposed_structures {
/*import com.telepathicgrunt.repurposedstructures.world.structures.GenericJigsawStructure;
*///?}

public final class WorldgenDataProvider
{
	private static List<String> biomes = new ArrayList<>();
	private static Map<String, StructureNamespaceData> structureNamespaceData = new TreeMap<>();
	private static Map<String, StructureData> structureData = new TreeMap<>();
	private static Map<String, StructureSetData> structureSetData = new TreeMap<>();
	private static Map<String, StructureTemplatePoolData> structureTemplatePoolsData = new TreeMap<>();

	public static List<String> getBiomes() {
		return biomes;
	}

	public static Map<String, StructureNamespaceData> getStructureNamespaces() {
		return structureNamespaceData;
	}

	public static Map<String, StructureData> getStructures() {
		return structureData;
	}

	public static Map<String, StructureSetData> getStructureSets() {
		return structureSetData;
	}

	public static Map<String, StructureTemplatePoolData> getStructureTemplatePools() {
		return structureTemplatePoolsData;
	}

	public static void loadWorldgenData() {
		biomes = loadBiomes();
		structureNamespaceData = loadStructureNamespaces();
		structureData = loadStructures();
		structureSetData = loadStructureSets();
		structureTemplatePoolsData = loadStructureTemplatePools();
	}

	public static List<String> loadBiomes() {
		var biomeRegistry = StructurifyRegistryManagerProvider.getBiomeRegistry();

		if (biomeRegistry == null) {
			return Collections.emptyList();
		}

		List<String> biomes = new ArrayList<>();

		for (var biomeTag : biomeRegistry.listTags().toList()) {
			biomes.add('#' + biomeTag.unwrapKey().get().location().toString());
		}

		for (var biome : biomeRegistry.listElements().toList()) {
			biomes.add(biome.unwrapKey().get()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString());
		}

		return biomes;
	}

	public static Map<String, StructureNamespaceData> loadStructureNamespaces() {
		Map<String, StructureNamespaceData> structuresNamespaces = new TreeMap<>(StructurifyComparators.ALPHABETICALL_ID_COMPARATOR);
		structuresNamespaces.put(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER, new StructureNamespaceData());

		var structureRegistry = StructurifyRegistryManagerProvider.getStructureRegistry();

		if (structureRegistry == null) {
			return structuresNamespaces;
		}

		for (var structureReference : structureRegistry.listElements().toList()) {
			String structureNamespace = structureReference.key()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.getNamespace();

			if (!structuresNamespaces.containsKey(structureNamespace)) {
				structuresNamespaces.put(structureNamespace, new StructureNamespaceData());
			}
		}

		return structuresNamespaces;
	}

	public static Map<String, StructureData> loadStructures() {
		var structureRegistry = StructurifyRegistryManagerProvider.getStructureRegistry();
		var biomeRegistry = StructurifyRegistryManagerProvider.getBiomeRegistry();

		if (structureRegistry == null || biomeRegistry == null) {
			return Collections.emptyMap();
		}

		Map<String, StructureData> structures = new TreeMap<>(StructurifyComparators.ALPHABETICALL_ID_COMPARATOR);

		for (var structureReference : structureRegistry.listElements().toList()) {
			Structure structure = structureReference.value();
			Identifier structureIdentifier = structureReference.key()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/;
			String structureId = structureIdentifier.toString();
			var biomeStorage = structure.biomes().unwrap();
			var defaultBiomes = new ArrayList<String>();

			biomeStorage.mapLeft(biomeTagKey -> {
				biomeRegistry.get(biomeTagKey).ifPresent(biomes -> {
					for (var biome : biomes) {
						String biomeKey = biome.unwrapKey().get()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();

						if (defaultBiomes.contains(biomeKey)) {
							continue;
						}

						defaultBiomes.add(biomeKey);
					}
				});

				return null;
			});

			biomeStorage.mapRight(biomes -> {
				for (var biome : biomes) {
					String biomeKey = biome.unwrapKey().get()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();

					if (defaultBiomes.contains(biomeKey)) {
						continue;
					}

					defaultBiomes.add(biomeKey);
				}

				return null;
			});

			StructureData structureData = new StructureData(defaultBiomes, structure.step(), structure.terrainAdaptation());
			@Nullable JsonObject structureJson = JigsawStructureUtil.getStructureData(structure);

			if (JigsawStructureUtil.isJigsawLikeStructure(structure, structureJson)) {
				var maxDistanceFromCenter = JigsawStructureUtil.getMaxDistanceFromCenterForStructure(structure, structureJson);

				Integer horizontalMaxDistanceFromCenter = null;
				Integer verticalMaxDistanceFromCenter = null;

				if(maxDistanceFromCenter != null) {
					//? if >= 1.21.9 {
					horizontalMaxDistanceFromCenter = maxDistanceFromCenter.horizontal();
					verticalMaxDistanceFromCenter = maxDistanceFromCenter.vertical();
					//?} else {
					/*horizontalMaxDistanceFromCenter = maxDistanceFromCenter;
					verticalMaxDistanceFromCenter = maxDistanceFromCenter;
					*///?}
				}

				var maxSize = JigsawStructureUtil.getSizeForStructure(structure, structureJson);
				var heightProvider = JigsawStructureUtil.getHeightProviderForStructure(structure, structureJson);
				var heightProviderData = HeightProviderData.fromHeightProvider(heightProvider);

				var projectStartToHeightmap = JigsawStructureUtil.getProjectStartToHeightMap(structure, structureJson);
				@Nullable ProjectStartToHeightmap projectStartToHeightmapOption;

				if(projectStartToHeightmap == null) {
					projectStartToHeightmapOption = null;
				} else {
					projectStartToHeightmapOption = ProjectStartToHeightmap.fromDataValue(projectStartToHeightmap);
				}

				structureData.setJigsawData(new JigsawData(maxSize, horizontalMaxDistanceFromCenter, verticalMaxDistanceFromCenter, heightProviderData, projectStartToHeightmapOption));
			}

			HolderSet<Biome> defaultBiomeHolders = BiomeUtil.getBiomeHolders(defaultBiomes, biomeRegistry);

			if (BiomeUtil.isWaterStructure(defaultBiomeHolders)) {
				var flatnessCheckData = structureData.getFlatnessCheckData();
				flatnessCheckData.defaultOverrideGlobalFlatnessCheck(true);
				flatnessCheckData.overrideGlobalFlatnessCheck(true);
				flatnessCheckData.defaultEnable(false);
				flatnessCheckData.enable(false);

				var biomeCheckData = structureData.getBiomeCheckData();
				biomeCheckData.defaultOverrideGlobalBiomeCheck(true);
				biomeCheckData.overrideGlobalBiomeCheck(true);
				biomeCheckData.defaultEnable(false);
				biomeCheckData.enable(false);
			}

			if (BiomeUtil.isSwampStructure(defaultBiomeHolders)) {
				var flatnessCheckData = structureData.getFlatnessCheckData();
				flatnessCheckData.defaultOverrideGlobalFlatnessCheck(true);
				flatnessCheckData.overrideGlobalFlatnessCheck(true);
				flatnessCheckData.defaultEnable(false);
				flatnessCheckData.enable(false);
			}

			if (StructureUtil.isUndergroundStructure(structureData.getStep())) {
				var flatnessCheckData = structureData.getFlatnessCheckData();
				flatnessCheckData.defaultOverrideGlobalFlatnessCheck(true);
				flatnessCheckData.overrideGlobalFlatnessCheck(true);
				flatnessCheckData.defaultEnable(false);
				flatnessCheckData.enable(false);

				var biomeCheckData = structureData.getBiomeCheckData();
				biomeCheckData.defaultOverrideGlobalBiomeCheck(true);
				biomeCheckData.overrideGlobalBiomeCheck(true);
				biomeCheckData.defaultEnable(false);
				biomeCheckData.enable(false);
			}

			if (StructureUtil.isRawGenerationStructure(structureData.getStep())) {
				var overlapCheckData = structureData.getOverlapCheckData();
				overlapCheckData.defaultExcludeFromOverlapPrevention(true);
				overlapCheckData.excludeFromOverlapPrevention(true);
			}

			if (structureId.equals("minecraft:shipwreck_beached")) {
				var flatnessCheckData = structureData.getFlatnessCheckData();
				flatnessCheckData.defaultOverrideGlobalFlatnessCheck(true);
				flatnessCheckData.overrideGlobalFlatnessCheck(true);
				flatnessCheckData.defaultEnable(false);
				flatnessCheckData.enable(false);
			}

			if (structureId.equals("nova_structures:illager_camp")) {
				var biomes = structureData.getBiomes();

				if (biomes.contains("minecraft:river")) {
					biomes.remove("minecraft:river");
				}
			}

			// Towns and Towers uses "fluid_springs" step for its structures
			if (structureId.contains("towns_and_towers")) {
				var flatnessCheckData = structureData.getFlatnessCheckData();
				flatnessCheckData.defaultOverrideGlobalFlatnessCheck(false);
				flatnessCheckData.overrideGlobalFlatnessCheck(false);
				flatnessCheckData.defaultEnable(false);
				flatnessCheckData.enable(false);

				var biomeCheckData = structureData.getBiomeCheckData();
				biomeCheckData.defaultOverrideGlobalBiomeCheck(false);
				biomeCheckData.overrideGlobalBiomeCheck(false);
				biomeCheckData.defaultEnable(false);
				biomeCheckData.enable(false);
			}

			if (structureId.contains("aquamirae:") || (structureId.contains("minecells:") && !structureId.contains("minecells:overworld_portal"))) {
				var overlapCheckData = structureData.getOverlapCheckData();
				overlapCheckData.defaultExcludeFromOverlapPrevention(true);
				overlapCheckData.excludeFromOverlapPrevention(true);

				var flatnessCheckData = structureData.getFlatnessCheckData();
				flatnessCheckData.overrideGlobalFlatnessCheck(true);
				flatnessCheckData.defaultOverrideGlobalFlatnessCheck(true);
				flatnessCheckData.enable(false);
				flatnessCheckData.defaultEnable(false);

				var biomeCheckData = structureData.getBiomeCheckData();
				biomeCheckData.overrideGlobalBiomeCheck(true);
				biomeCheckData.defaultOverrideGlobalBiomeCheck(true);
				biomeCheckData.enable(false);
				biomeCheckData.defaultEnable(false);
			}

			if (
				structureId.equals("alexscaves:acid_pit")
				|| structureId.equals("alexscaves:cake_cave")
				|| structureId.equals("alexscaves:dino_bowl")
				|| structureId.equals("alexscaves:ferrocave")
				|| structureId.equals("alexscaves:forlorn_canyon")
				|| structureId.equals("alexscaves:ocean_trench")
			) {
				var flatnessCheckData = structureData.getFlatnessCheckData();
				flatnessCheckData.overrideGlobalFlatnessCheck(true);
				flatnessCheckData.defaultOverrideGlobalFlatnessCheck(true);
				flatnessCheckData.enable(false);
				flatnessCheckData.defaultEnable(false);

				var biomeCheckData = structureData.getBiomeCheckData();
				biomeCheckData.overrideGlobalBiomeCheck(true);
				biomeCheckData.defaultOverrideGlobalBiomeCheck(true);
				biomeCheckData.enable(false);
				biomeCheckData.defaultEnable(false);
			}

			structures.put(structureId, structureData);
		}

		return structures;
	}

	public static Map<String, StructureSetData> loadStructureSets() {
		var structureSetRegistry = StructurifyRegistryManagerProvider.getStructureSetRegistry();

		if (structureSetRegistry == null) {
			return Collections.emptyMap();
		}

		Map<String, StructureSetData> structureSets = new TreeMap<>();

		for (var structureSetReference : structureSetRegistry.listElements().toList()) {
			var structureSet = structureSetReference.value();
			String structureSetId = structureSetReference.key()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();
			((StructurifyWithStructureSet) (Object) structureSet).structurify$setStructureSetId(structureSetId);

			var structureSetPlacement = structureSet.placement();
			int salt = 0;
			float frequency = 0.0f;
			int spacing = 0;
			int separation = 0;

			if (structureSetPlacement instanceof StructurifyStructurePlacement structurePlacement) {
				salt = structurePlacement.structurify$getOriginalSalt();
				frequency = structurePlacement.structurify$getOriginalFrequency();
			}

			if (structureSetPlacement instanceof StructurifyRandomSpreadStructurePlacement randomSpreadStructurePlacement) {
				spacing = randomSpreadStructurePlacement.structurify$getOriginalSpacing();
				separation = randomSpreadStructurePlacement.structurify$getOriginalSeparation();
			}

			var structureWeights = new HashMap<String, Integer>();

			for (var structureSelectionEntry : structureSet.structures()) {
				StructurifyStructureSelectionEntry structurifyStructureSelectionEntry = ((StructurifyStructureSelectionEntry) (Object) structureSelectionEntry);
				var structureId = structureSelectionEntry.structure().unwrapKey().get()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();
				structureWeights.put(structureId, structurifyStructureSelectionEntry.structurify$getOriginalWeight());
			}

			var structureSetData = new StructureSetData(
				salt,
				frequency,
				spacing,
				separation,
				structureWeights
			);

			if (
				structureSetId.equals("alexscaves:acid_pit")
				|| structureSetId.equals("alexscaves:cake_cave")
				|| structureSetId.equals("alexscaves:dino_bowl")
				|| structureSetId.equals("alexscaves:ferrocave")
				|| structureSetId.equals("alexscaves:forlorn_canyon")
				|| structureSetId.equals("alexscaves:ocean_trench")
				|| (structureSetId.contains("minecells:") && !structureSetId.contains("minecells:overworld_portal"))
			) {
				structureSetData.setOverrideGlobalSpacingAndSeparationModifier(true);
				structureSetData.setDefaultOverrideGlobalSpacingAndSeparationModifier(true);
			}

			structureSets.put(structureSetId, structureSetData);
		}

		return structureSets;
	}

	public static Map<String, StructureTemplatePoolData> loadStructureTemplatePools() {
		var structureTemplatePoolReferences = StructurifyTemplatePoolProvider.getStructureTemplatePoolElementsWithWeight();
		Map<String, StructureTemplatePoolData> structureTemplatePools = new TreeMap<>();

		for(var structureTemplatePoolReference : structureTemplatePoolReferences.entrySet()) {
			var structureTemplatePooId = structureTemplatePoolReference.getKey();
			var structureTemplatePoolElements = structureTemplatePoolReference.getValue();
			var structureTemplatePoolData = new StructureTemplatePoolData(structureTemplatePoolElements);

			structureTemplatePools.put(structureTemplatePooId, structureTemplatePoolData);
		}

		return structureTemplatePools;
	}
}
