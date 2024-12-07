package com.faboslav.structurify.common.config;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;

import java.util.List;
import java.util.Map;

public final class StructurifyConfigLoader
{
	public static void loadConfig(final LoadConfigEvent event) {
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
			.filter(entry -> !entry.getValue().isUsingDefaultValues())
			.map(Map.Entry::getKey)
			.toList();

		if (Structurify.getConfig().enableGlobalSpacingAndSeparationModifier && Structurify.getConfig().globalSpacingAndSeparationModifier != 1.0D) {
			Structurify.getLogger().info("Enabled global spacing and separation modifier with value of {}", Structurify.getConfig().globalSpacingAndSeparationModifier);
		}

		if (!changedStructureSets.isEmpty()) {
			Structurify.getLogger().info("Changed spacing and/or separation of {} structures sets: {}", changedStructureSets.size(), changedStructureSets);
		}
	}
}
