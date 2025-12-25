package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.api.StructurifyStructurePlacement;
import com.faboslav.structurify.common.config.data.structure.JigsawData;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.faboslav.structurify.common.util.BiomeUtil;
import com.faboslav.structurify.common.util.JigsawStructureUtil;
import com.faboslav.structurify.common.util.StructureUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.*;

//? if yungs_api || repurposed_structures {
import com.faboslav.structurify.common.platform.PlatformHooks;
//?}

//? if yungs_api {
/*import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
 *///?}

//? if repurposed_structures {
import com.telepathicgrunt.repurposedstructures.world.structures.GenericJigsawStructure;
//?}

public final class WorldgenDataProvider
{
	private static List<String> biomes = new ArrayList<>();
	private static Map<String, StructureNamespaceData> structureNamespaceData = new TreeMap<>();
	private static Map<String, StructureData> structureData = new TreeMap<>();
	private static Map<String, StructureSetData> structureSetData = new TreeMap<>();

	private static final Comparator<String> alphabeticallComparator = (key1, key2) -> {
		boolean isKey1Minecraft = key1.startsWith("minecraft:");
		boolean isKey2Minecraft = key2.startsWith("minecraft:");

		if (isKey1Minecraft && !isKey2Minecraft) {
			return -1;
		} else if (!isKey1Minecraft && isKey2Minecraft) {
			return 1;
		} else {
			return key1.compareTo(key2);
		}
	};

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

	public static void loadWorldgenData() {
		biomes = loadBiomes();
		structureNamespaceData = loadStructureNamespaces();
		structureData = loadStructures();
		structureSetData = loadStructureSets();
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
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return Collections.emptyMap();
		}

		var structureRegistry = registryManager.lookupOrThrow(Registries.STRUCTURE);
		Map<String, StructureNamespaceData> structuresNamespaces = new TreeMap<>(alphabeticallComparator);

		for (var structureReference : structureRegistry.listElements().toList()) {
			String structureNamespace = structureReference.key()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.getNamespace();

			if (!structuresNamespaces.containsKey(structureNamespace)) {
				structuresNamespaces.put(structureNamespace, new StructureNamespaceData());
			}
		}

		structuresNamespaces.put(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER, new StructureNamespaceData());

		return structuresNamespaces;
	}

	public static Map<String, StructureData> loadStructures() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return Collections.emptyMap();
		}

		var structureRegistry = registryManager.lookupOrThrow(Registries.STRUCTURE);
		var biomeRegistry = registryManager.lookupOrThrow(Registries.BIOME);
		Map<String, StructureData> structures = new TreeMap<>(alphabeticallComparator);

		for (var structureReference : structureRegistry.listElements().toList()) {
			var structure = structureReference.value();
			String structureId = structureReference.key()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();
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

			if (JigsawStructureUtil.isJigsawLikeStructure(structure)) {
				int horizontalMaxDistanceFromCenter;
				int verticalMaxDistanceFromCenter;

				//? if >= 1.21.9 {
				var maxDistanceFromCenter = JigsawStructureUtil.getMaxDistanceFromCenterForStructure(structure);
				horizontalMaxDistanceFromCenter = maxDistanceFromCenter.horizontal();
				verticalMaxDistanceFromCenter = maxDistanceFromCenter.vertical();
				//?} else {
				/*horizontalMaxDistanceFromCenter = JigsawStructureUtil.getMaxDistanceFromCenterForStructure(structure);
				verticalMaxDistanceFromCenter = JigsawStructureUtil.getMaxDistanceFromCenterForStructure(structure);
				*///?}
				int maxSize = JigsawStructureUtil.getSizeForStructure(structure);
				structureData.setJigsawData(new JigsawData(maxSize, horizontalMaxDistanceFromCenter, verticalMaxDistanceFromCenter));
			}

			HolderSet<Biome> defaultBiomeHolders = BiomeUtil.getBiomeHolders(defaultBiomes, biomeRegistry);

			if(BiomeUtil.isWaterStructure(defaultBiomeHolders)) {
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

			if (StructureUtil.isUndergroundStructure(structureData.getStep())) {
				var flatnessCheckData = structureData.getFlatnessCheckData();
				flatnessCheckData.defaultOverrideGlobalFlatnessCheck(true);
				flatnessCheckData.overrideGlobalFlatnessCheck(true);
				flatnessCheckData.defaultEnable(false);
				flatnessCheckData.enable(false);
			}

			if (StructureUtil.isRawGenerationStructure(structureData.getStep())) {
				var overlapCheckData = structureData.getOverlapCheckData();
				overlapCheckData.defaultExcludeFromOverlapPrevention(true);
				overlapCheckData.excludeFromOverlapPrevention(true);
			}

			if(structureId.equals("minecraft:shipwreck_beached")) {
				var flatnessCheckData = structureData.getFlatnessCheckData();
				flatnessCheckData.defaultOverrideGlobalFlatnessCheck(true);
				flatnessCheckData.overrideGlobalFlatnessCheck(true);
				flatnessCheckData.defaultEnable(false);
				flatnessCheckData.enable(false);
			}

			if(structureId.equals("nova_structures:illager_camp")) {
				var biomes = structureData.getBiomes();

				if(biomes.contains("minecraft:river")) {
					biomes.remove("minecraft:river");
				}
			}

			if(structureId.contains("aquamirae:") || (structureId.contains("minecells:") && !structureId.contains("minecells:overworld_portal"))) {
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

			if(
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
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return Collections.emptyMap();
		}

		var structureSetRegistry = registryManager.lookupOrThrow(Registries.STRUCTURE_SET);
		Map<String, StructureSetData> structureSets = new TreeMap<>();

		for (var structureSetReference : structureSetRegistry.listElements().toList()) {
			var structureSet = structureSetReference.value();
			Identifier structureSetId = structureSetReference.key()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/;
			String structureSetStringId = structureSetId.toString();

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

			var structureSetData = new StructureSetData(
				salt,
				frequency,
				spacing,
				separation
			);

			if(
				structureSetStringId.equals("alexscaves:acid_pit")
				|| structureSetStringId.equals("alexscaves:cake_cave")
				|| structureSetStringId.equals("alexscaves:dino_bowl")
				|| structureSetStringId.equals("alexscaves:ferrocave")
				|| structureSetStringId.equals("alexscaves:forlorn_canyon")
				|| structureSetStringId.equals("alexscaves:ocean_trench")
				|| (structureSetStringId.contains("minecells:") && !structureSetStringId.contains("minecells:overworld_portal"))
			) {
				structureSetData.setOverrideGlobalSpacingAndSeparationModifier(true);
				structureSetData.setDefaultOverrideGlobalSpacingAndSeparationModifier(true);
			}

			structureSets.put(structureSetStringId, structureSetData);
		}

		return structureSets;
	}
}
