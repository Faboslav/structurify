package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.api.StructurifyStructurePlacement;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public final class StructurifyRegistryUpdater
{
	public static void updateRegistries(final UpdateRegistriesEvent event) {
		if (!Structurify.getConfig().isLoaded) {
			return;
		}

		try {
			Structurify.getLogger().info("Updating registries...");
			var registryManager = event.registryManager();

			if (registryManager == null) {
				return;
			}

			StructurifyRegistryUpdater.updateStructures(registryManager);
			StructurifyRegistryUpdater.updateStructureSets(registryManager);
			Structurify.getLogger().info("Registries updated");
		} catch (Exception e) {
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

			ResourceLocation structureId = structureRegistryKey.location();
			var structurifyStructure = ((StructurifyStructure) structure);
			structurifyStructure.structurify$setStructureIdentifier(structureId);
			structurifyStructure.structurify$setStructureBiomes(null);
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

			ResourceLocation structureSetId = structureSetRegistryKey.location();
			StructurifyStructurePlacement structurifyStructurePlacement = ((StructurifyStructurePlacement) structureSet.placement());
			structurifyStructurePlacement.structurify$setStructureSetIdentifier(structureSetId);
		}

		Structurify.getLogger().info("Structure Sets registries updated");
	}
}
