package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.api.StructurifyStructurePlacement;
import com.faboslav.structurify.common.mixin.structure.jigsaw.MaxDistanceFromCenterAccessor;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.lang.reflect.Field;
import java.util.*;

//? yungs_api || repurposed_structures {
/*import com.faboslav.structurify.common.platform.PlatformHooks;
*///?}

//? yungs_api {
/*import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
 *///?}

//? repurposed_structures {
/*import com.telepathicgrunt.repurposedstructures.world.structures.GenericJigsawStructure;
*///?}

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
		var biomeRegistry = StructurifyRegistryManagerProvider.getBiomeRegistry();

		if (biomeRegistry == null) {
			return Collections.emptyList();
		}

		List<String> biomes = new ArrayList<>();

		for (var biomeTag : biomeRegistry.listTags().toList()) {
			biomes.add('#' + biomeTag.unwrapKey().get().location().toString());
		}

		for (var biome : biomeRegistry.listElements().toList()) {
			biomes.add(biome.unwrapKey().get().location().toString());
		}

		return biomes;
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
			String structureId = structureReference.key().location().toString();
			var biomeStorage = structure.biomes().unwrap();
			var defaultBiomes = new ArrayList<String>();

			biomeStorage.mapLeft(biomeTagKey -> {
				biomeRegistry.get(biomeTagKey).ifPresent(biomes -> {
					for (var biome : biomes) {
						String biomeKey = biome.unwrapKey().get().location().toString();

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
					String biomeKey = biome.unwrapKey().get().location().toString();

					if (defaultBiomes.contains(biomeKey)) {
						continue;
					}

					defaultBiomes.add(biomeKey);
				}

				return null;
			});

			int checkRadius = getCheckRadiusForStructure(structure);

			structures.put(structureId, new StructureData(defaultBiomes, checkRadius));
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
			ResourceLocation structureSetId = structureSetReference.key().location();
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

			if (structureSet.placement() instanceof StructurePlacement) {
				structureSets.put(structureSetStringId, new StructureSetData(
					salt,
					frequency,
					spacing,
					separation
				));
			}
		}

		return structureSets;
	}

	private static int getCheckRadiusForStructure(Structure structure) {
		if (structure instanceof JigsawStructure) {
			return ((MaxDistanceFromCenterAccessor) structure).structurify$getMaxDistanceFromCenter();
		}

		//? yungs_api {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("yungsapi") && structure instanceof YungJigsawStructure) {
			return ((YungJigsawStructure) structure).maxDistanceFromCenter;
		}
		*///?}

		//? repurposed_structures {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("repurposed_structures") && structure instanceof GenericJigsawStructure) {
			return ((GenericJigsawStructure) structure).maxDistanceFromCenter.orElse(0);
		}
		*///?}

		Class<?> clazz = structure.getClass();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (field.getName().equals("maxDistanceFromCenter")) {
				field.setAccessible(true);

				try {
					if (Optional.class.isAssignableFrom(field.getType())) {
						Optional<?> optionalValue = (Optional<?>) field.get(structure);
						return optionalValue.map(val -> (Integer) val).orElse(0);
					} else {
						return field.getInt(structure);
					}
				} catch (IllegalAccessException | IllegalArgumentException e) {
					Structurify.getLogger().error(e.getMessage());
				}

				break;
			}
		}

		return 0;
	}
}
