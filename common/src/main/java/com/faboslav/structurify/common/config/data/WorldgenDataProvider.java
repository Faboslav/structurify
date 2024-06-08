package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.structure.Structure;

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

	public static void reload() {
		StructurifyRegistryManagerProvider.reloadRegistryManager();
		biomes = loadBiomes();
		structureData = loadStructures();
		structureSetData = loadStructureSets();
	}

	public static List<String> loadBiomes() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return Collections.emptyList();
		}

		var biomeRegistry = registryManager.get(RegistryKeys.BIOME);
		List<String> biomes = new ArrayList<>();

		for (var biome : biomeRegistry.streamEntries().toList()) {
			biomes.add(biome.getKey().get().getValue().toString());
		}

		return biomes;
	}

	public static Map<String, StructureData> loadStructures() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return Collections.emptyMap();
		}

		var structureRegistry = registryManager.get(RegistryKeys.STRUCTURE);
		var biomeRegistry = registryManager.get(RegistryKeys.BIOME);
		Map<String, StructureData> structures = new TreeMap<>(alphabeticallComparator);

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

			Class<?> clazz = structure.getClass();
			Field[] fields = clazz.getDeclaredFields();
			boolean hasMaxDistanceFromCenter = false;

			for (Field field : fields) {
				if (field.getName().equals("maxDistanceFromCenter")) {
					hasMaxDistanceFromCenter = true;
					break;
				}
			}

			structures.put(structureId, new StructureData(defaultBiomes, !hasMaxDistanceFromCenter));
		}

		return structures;
	}

	public static Map<String, StructureSetData> loadStructureSets() {
		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
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

			if (structureSet.placement() instanceof RandomSpreadStructurePlacement randomSpreadStructurePlacement) {
				structureSets.put(structureSetStringId, new StructureSetData(randomSpreadStructurePlacement.getSpacing(), randomSpreadStructurePlacement.getSeparation()));
			}
		}

		return structureSets;
	}
}
