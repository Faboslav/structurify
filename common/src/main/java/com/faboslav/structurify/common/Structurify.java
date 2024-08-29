package com.faboslav.structurify.common;

import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.PrepareRegistriesEvent;
import com.faboslav.structurify.common.modcompat.ModChecker;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

public final class Structurify {
	public static final String MOD_ID = "structurify";
	private static final Logger LOGGER = LoggerFactory.getLogger(Structurify.MOD_ID);
	private static final StructurifyConfig CONFIG = new StructurifyConfig();

	public static String makeStringID(String name) {
		return MOD_ID + ":" + name;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public static StructurifyConfig getConfig() {
		return CONFIG;
	}

	public static ResourceLocation makeId(String path) {
		/*? >=1.21 {*/
		/*return ResourceLocation.tryBuild(
			MOD_ID,
			path
		);
		 *//*?} else {*/
		return new ResourceLocation(
			MOD_ID,
			path
		);
		/*?}*/
	}

	public static ResourceLocation makeVanillaId(String id) {
		/*? >=1.21 {*/
		/*return ResourceLocation.parse(
			id
		);
		*//*?} else {*/
		return new ResourceLocation(
			id
		);
		/*?}*/
	}

	public static void init() {
		Structurify.getConfig().create();
		ModChecker.setupModCompat();

		LoadConfigEvent.EVENT.addListener(Structurify::loadConfig);
		PrepareRegistriesEvent.EVENT.addListener(Structurify::prepareRegistries);
		// DatapackReloadEvent.EVENT.addListener(Structurify::reloadStructurifyRegistryManager);
	}

	private static void loadConfig(final LoadConfigEvent event) {
		if (Structurify.getConfig().isLoaded) {
			return;
		}

		Structurify.getConfig().load();

		List<String> disabledStructures = Structurify.getConfig().getStructureData().entrySet()
			.stream()
			.filter(entry -> entry.getValue().isDisabled())
			.map(Map.Entry::getKey)
			.toList();

		if (!disabledStructures.isEmpty()) {
			Structurify.getLogger().info("Disabled {} structures: {}", disabledStructures.size(), disabledStructures);
		}

		List<String> changedStructureSets = Structurify.getConfig().getStructureSetData().entrySet()
			.stream()
			.filter(entry -> !entry.getValue().isUsingDefaultSpacingAndSeparation())
			.map(Map.Entry::getKey)
			.toList();

		if (Structurify.getConfig().enableGlobalSpacingAndSeparationModifier && Structurify.getConfig().globalSpacingAndSeparationModifier != 1.0D) {
			Structurify.getLogger().info("Enabled global spacing and separation modifier with value of {}", Structurify.getConfig().globalSpacingAndSeparationModifier);
		}

		if (!changedStructureSets.isEmpty()) {
			Structurify.getLogger().info("Changed spacing and/or separation of {} structures sets: {}", changedStructureSets.size(), changedStructureSets);
		}
	}

	/*
	private static void reloadStructurifyRegistryManager(final DatapackReloadEvent event) {
		WorldgenDataProvider.reload();
	} */

	private static void prepareRegistries(final PrepareRegistriesEvent event) {
		if (!Structurify.getConfig().isLoaded) {
			return;
		}

		var registryManager = event.registryManager();

		if (registryManager == null) {
			return;
		}

		Structurify.prepareStructures(registryManager);
		Structurify.prepareStructureSets(registryManager);
	}

	private static void prepareStructures(RegistryAccess registryManager) {
		var structureRegistry = registryManager.registry(Registries.STRUCTURE).orElse(null);

		if (structureRegistry == null) {
			return;
		}

		for (var structure : structureRegistry) {
			ResourceKey<Structure> structureRegistryKey = structureRegistry.getResourceKey(structure).orElse(null);

			if (structureRegistryKey == null) {
				continue;
			}

			ResourceLocation structureId = structureRegistryKey.location();
			((StructurifyStructure) structure).structurify$setStructureIdentifier(structureId);
		}
	}

	private static void prepareStructureSets(RegistryAccess registryManager) {
		var structureSetRegistry = registryManager.registry(Registries.STRUCTURE_SET).orElse(null);

		if (structureSetRegistry == null) {
			return;
		}

		for (StructureSet structureSet : structureSetRegistry) {
			ResourceKey<StructureSet> structureSetRegistryKey = structureSetRegistry.getResourceKey(structureSet).orElse(null);

			if (structureSetRegistryKey == null) {
				continue;
			}

			ResourceLocation structureSetId = structureSetRegistryKey.location();

			if (structureSet.placement() instanceof RandomSpreadStructurePlacement randomSpreadStructurePlacement) {
				((StructurifyRandomSpreadStructurePlacement) randomSpreadStructurePlacement).structurify$setStructureSetIdentifier(structureSetId);
			}
		}
	}
}