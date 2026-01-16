package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.api.StructurifyStructurePlacement;
import com.faboslav.structurify.common.api.StructurifyWithStructureSet;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public final class StructurifyRegistryUpdater
{
	public static void updateRegistries(final UpdateRegistriesEvent event) {
		if (!Structurify.getConfig().isLoaded) {
			Structurify.getLogger().info("Registries not updated, config not loaded");
			return;
		}

		try {
			Structurify.getLogger().info("Updating registries...");
			var registryManager = event.registryManager();

			if (registryManager == null) {
				Structurify.getLogger().info("Registries not updated, registry manager not loaded");
				return;
			}

			StructurifyRegistryUpdater.updateStructures(registryManager);
			StructurifyRegistryUpdater.updateStructureSets(registryManager);
			Structurify.getLogger().info("Registries updated");
		} catch (Exception e) {
			Structurify.getLogger().error(String.valueOf(e));
			Structurify.getLogger().info("Failed to update registries");
		}
	}

	private static void updateStructures(HolderLookup.Provider registryManager) {
		var structureRegistry = registryManager.lookup(Registries.STRUCTURE).orElse(null);

		if (structureRegistry == null) {
			return;
		}

		for (var structureReference : structureRegistry.listElements().toList()) {
			var structure = structureReference.value();
			var structureRegistryKey = structureReference.key();

			Identifier structureId = structureRegistryKey/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/;
			var structurifyStructure = ((StructurifyStructure) structure);
			structurifyStructure.structurify$setStructureIdentifier(structureId);
		}

		Structurify.getLogger().info("Structure registries updated");
	}

	private static void updateStructureSets(HolderLookup.Provider registryManager) {
		var structureSetRegistry = registryManager.lookup(Registries.STRUCTURE_SET).orElse(null);

		if (structureSetRegistry == null) {
			return;
		}

		for (var structureSetReference : structureSetRegistry.listElements().toList()) {
			var structureSet = structureSetReference.value();
			var structureSetRegistryKey = structureSetReference.key();
			String structureSetId = structureSetRegistryKey/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();

			((StructurifyWithStructureSet) (Object) structureSet).structurify$setStructureSetId(structureSetId);
			StructurifyStructurePlacement structurifyStructurePlacement = ((StructurifyStructurePlacement) structureSet.placement());
			structurifyStructurePlacement.structurify$setStructureSetId(structureSetId);

			var structures = structureSet.structures();

			for(var structure : structures) {
				((StructurifyWithStructureSet) (Object) structure).structurify$setStructureSetId(structureSetId);
			}
		}

		Structurify.getLogger().info("Structure Sets registries updated");
	}
}
