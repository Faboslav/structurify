package com.faboslav.structurify.common;

import com.faboslav.structurify.common.api.RandomSpreadStructurePlacement;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.events.lifecycle.DatapackReloadEvent;
import com.faboslav.structurify.common.events.lifecycle.TagsUpdatedEvent;
import com.faboslav.structurify.common.modcompat.ModChecker;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Structurify
{
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

	public static Identifier makeID(String path) {
		return new Identifier(
			MOD_ID,
			path
		);
	}

	public static void init() {
		ModChecker.setupModCompat();

		TagsUpdatedEvent.EVENT.addListener(Structurify::prepareStructureSets);
		DatapackReloadEvent.EVENT.addListener(Structurify::reloadStructurifyRegistryManager);
	}

	private static void reloadStructurifyRegistryManager(final DatapackReloadEvent event) {
		Structurify.getLogger().info("DatapackReloadEvent");
		StructurifyRegistryManagerProvider.reloadRegistryManager();
	}

	private static void prepareStructureSets(final TagsUpdatedEvent event) {
		Structurify.getLogger().info("TagsUpdatedEvent");
		if (!Structurify.getConfig().isLoaded) {
			return;
		}

		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if(registryManager == null) {
			return;
		}

		var structureSetRegistry = registryManager.get(RegistryKeys.STRUCTURE_SET);

		for (StructureSet structureSet : structureSetRegistry) {
			RegistryKey<StructureSet> structureSetRegistryKey = structureSetRegistry.getKey(structureSet).orElse(null);

			if (structureSetRegistryKey == null) {
				continue;
			}

			Identifier structureSetId = structureSetRegistryKey.getValue();

			if (structureSet.placement() instanceof net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement randomSpreadStructurePlacement) {
				((RandomSpreadStructurePlacement) randomSpreadStructurePlacement).structurify$setStructureIdentifier(structureSetId);
			}
		}

		Structurify.getLogger().info("tags loaded");
	}
}
