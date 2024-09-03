package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.mixin.structure.jigsaw.MaxDistanceFromCenterAccessor;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.faboslav.structurify.common.util.Platform;
import com.telepathicgrunt.repurposedstructures.world.structures.GenericJigsawStructure;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import java.lang.reflect.Field;
import java.util.*;

public final class WorldgenDataProvider
{
	private static List<String> biomes = new ArrayList<>();
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

	public static Map<String, StructureData> getStructures() {
		return structureData;
	}

	public static Map<String, StructureSetData> getStructureSets() {
		return structureSetData;
	}

	public static void loadWorldgenData() {
		biomes = loadBiomes();
		structureData = loadStructures();
		structureSetData = loadStructureSets();
	}

	public static List<String> loadBiomes() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return Collections.emptyList();
		}

		var biomeRegistry = registryManager.registryOrThrow(Registries.BIOME);
		List<String> biomes = new ArrayList<>();

		for (var biome : biomeRegistry.holders().toList()) {
			biomes.add(biome.unwrapKey().get().location().toString());
		}

		return biomes;
	}

	public static Map<String, StructureData> loadStructures() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return Collections.emptyMap();
		}

		var structureRegistry = registryManager.registryOrThrow(Registries.STRUCTURE);
		var biomeRegistry = registryManager.registryOrThrow(Registries.BIOME);
		Map<String, StructureData> structures = new TreeMap<>(alphabeticallComparator);

		for (Structure structure : structureRegistry) {
			ResourceKey<Structure> structureRegistryKey = structureRegistry.getResourceKey(structure).orElse(null);

			if (structureRegistryKey == null) {
				continue;
			}

			String structureId = structureRegistryKey.location().toString();
			var biomeStorage = structure.biomes().unwrap();
			Set<String> defaultBiomes = new HashSet<>();

			biomeStorage.mapLeft(biomeTagKey -> {
				biomeRegistry.getTag(biomeTagKey).ifPresent(biomes -> {
					for (var biome : biomes) {
						String biomeKey = biome.unwrapKey().get().location().toString();
						defaultBiomes.add(biomeKey);
					}
				});

				return null;
			});

			int biomeRadiusCheck = 0;

			if (structure instanceof JigsawStructure) {
				biomeRadiusCheck = ((MaxDistanceFromCenterAccessor) structure).structurify$getMaxDistanceFromCenter();
			} else if (Platform.isModLoaded("yungsapi") && structure instanceof YungJigsawStructure) {
				biomeRadiusCheck = ((YungJigsawStructure) structure).maxDistanceFromCenter;
			} else if (Platform.isModLoaded("repurposed_structures") && structure instanceof GenericJigsawStructure) {
				biomeRadiusCheck = ((GenericJigsawStructure) structure).maxDistanceFromCenter.orElse(0);
			} else {
				Class<?> clazz = structure.getClass();
				Field[] fields = clazz.getDeclaredFields();

				for (Field field : fields) {
					if (field.getName().equals("maxDistanceFromCenter")) {
						field.setAccessible(true);

						try {
							biomeRadiusCheck = field.getInt(structure);
						} catch (IllegalAccessException e) {
						}

						break;
					}
				}
			}

			structures.put(structureId, new StructureData(defaultBiomes, biomeRadiusCheck));
		}

		return structures;
	}

	public static Map<String, StructureSetData> loadStructureSets() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return Collections.emptyMap();
		}

		var structureSetRegistry = registryManager.registryOrThrow(Registries.STRUCTURE_SET);
		Map<String, StructureSetData> structureSets = new TreeMap<>();

		for (StructureSet structureSet : structureSetRegistry) {
			ResourceKey<StructureSet> structureSetRegistryKey = structureSetRegistry.getResourceKey(structureSet).orElse(null);

			if (structureSetRegistryKey == null) {
				continue;
			}

			ResourceLocation structureSetId = structureSetRegistryKey.location();
			String structureSetStringId = structureSetId.toString();

			if (structureSet.placement() instanceof RandomSpreadStructurePlacement randomSpreadStructurePlacement) {
				structureSets.put(structureSetStringId, new StructureSetData(((StructurifyRandomSpreadStructurePlacement) randomSpreadStructurePlacement).structurify$getOriginalSpacing(), ((StructurifyRandomSpreadStructurePlacement) randomSpreadStructurePlacement).structurify$getOriginalSeparation()));
			}
		}

		return structureSets;
	}
}
