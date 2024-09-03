package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

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

	private static void updateStructures(RegistryAccess registryManager) {
		var structureRegistry = registryManager.registry(Registries.STRUCTURE).orElse(null);

		if (structureRegistry == null) {
			return;
		}

		for (Structure structure : structureRegistry) {
			ResourceKey<Structure> structureRegistryKey = structureRegistry.getResourceKey(structure).orElse(null);

			if (structureRegistryKey == null) {
				continue;
			}

			ResourceLocation structureId = structureRegistryKey.location();
			((StructurifyStructure) structure).structurify$setStructureIdentifier(structureId);
		}

		Structurify.getLogger().info("Structure registries updated");
	}

	private static void updateStructureSets(RegistryAccess registryManager) {
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

		Structurify.getLogger().info("Structure Sets registries updated");
	}
}
