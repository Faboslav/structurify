package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.api.StructurifyStructurePlacement;
import com.faboslav.structurify.common.api.StructurifyTemplatePool;
import com.faboslav.structurify.common.api.StructurifyWithStructureSet;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.mixin.structure.StructureTemplatePoolMixin;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

import java.util.ArrayList;
import java.util.List;

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
			StructurifyRegistryUpdater.updateStructureTemplatePools(registryManager);
			Structurify.getLogger().info("Registries updated");
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to update registries", e);
		}
	}

	private static void updateStructures(HolderLookup.Provider registryManager) {
		var structureRegistry = registryManager.lookup(Registries.STRUCTURE).orElse(null);

		if (structureRegistry == null) {
			return;
		}

		for (var structureReference : structureRegistry.listElements().toList()) {
			Structure structure = structureReference.value();
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

	private static void updateStructureTemplatePools(HolderLookup.Provider registryManager) {
		var structureTemplatePoolRegistry = registryManager.lookup(Registries.TEMPLATE_POOL).orElse(null);

		if (structureTemplatePoolRegistry == null) {
			return;
		}

		for (var structureTemplatePoolReference : structureTemplatePoolRegistry.listElements().toList()) {
			var structureTemplatePool = structureTemplatePoolReference.value();
			var structureTemplatePoolRegistryKey = structureTemplatePoolReference.key();
			String structureTemplatePoolId = structureTemplatePoolRegistryKey/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();
			StructureTemplatePoolMixin structurifyTemplatePool = ((StructureTemplatePoolMixin) structureTemplatePool);

			var structureTemplatePoolData = Structurify.getConfig().getStructureTemplatePoolsData().getOrDefault(structureTemplatePoolId, null); //StructurifyTemplatePoolProvider.getTemplatePoolElementWeights().get(structureTemplatePoolId);

			if (structureTemplatePoolData == null || structureTemplatePoolData.getStructureTemplatePoolElementWeights().isEmpty() || structureTemplatePoolData.isUsingDefaultValues()) {
				continue;
			}

			List<Pair<StructurePoolElement, Integer>> originalRawTemplates = List.copyOf(structurifyTemplatePool.getRawTemplates());
			ObjectArrayList<StructurePoolElement> originalTemplates = new ObjectArrayList<>(structurifyTemplatePool.getTemplates());
			int originalMaxSize = structurifyTemplatePool.getMaxSize();
			List<Pair<StructurePoolElement, Integer>> rawTemplates = new ArrayList<>();

			try {
				for (Pair<StructurePoolElement, Integer> originalRawTemplate : originalRawTemplates) {
					StructurePoolElement structurePoolElement = originalRawTemplate.getFirst();
					var structurePoolElementWeight = originalRawTemplate.getSecond();
					var structureTemplatePoolElementId = StructurifyTemplatePoolProvider.getStructurePoolElementLocation(structurePoolElement);

					if (structureTemplatePoolElementId == null) {
						rawTemplates.add(Pair.of(structurePoolElement, structurePoolElementWeight));
						continue;
					}

					var modifiedStructurePoolElementWeight = structureTemplatePoolData.getStructureTemplatePoolElementWeights().getOrDefault(structureTemplatePoolElementId, null);

					if(modifiedStructurePoolElementWeight != null && modifiedStructurePoolElementWeight.equals(0)) {
						continue;
					}

					if (modifiedStructurePoolElementWeight == null || modifiedStructurePoolElementWeight.equals(structurePoolElementWeight)) {
						rawTemplates.add(Pair.of(structurePoolElement, structurePoolElementWeight));
						continue;
					}

					rawTemplates.add(Pair.of(structurePoolElement, modifiedStructurePoolElementWeight));
				}

				if(rawTemplates.isEmpty()) {
					rawTemplates.add(Pair.of(EmptyPoolElement.INSTANCE, 1));
				}

				ObjectArrayList<StructurePoolElement> templates = new ObjectArrayList<>();

				for(Pair<StructurePoolElement, Integer> rawTemplate : rawTemplates) {
					StructurePoolElement element = rawTemplate.getFirst();

					for(int i = 0; i < rawTemplate.getSecond(); ++i) {
						templates.add(element);
					}
				}

				structurifyTemplatePool.setRawTemplates(rawTemplates);
				structurifyTemplatePool.setTemplates(templates);
				structurifyTemplatePool.setMaxSize(Integer.MIN_VALUE);
			} catch (Exception e) {
				structurifyTemplatePool.setRawTemplates(originalRawTemplates);
				structurifyTemplatePool.setTemplates(originalTemplates);
				structurifyTemplatePool.setMaxSize(originalMaxSize);
			}
		}

		Structurify.getLogger().info("Structure Template Pools registries updated");
	}
}
