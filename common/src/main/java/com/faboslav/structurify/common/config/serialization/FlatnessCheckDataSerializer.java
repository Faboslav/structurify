package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.google.gson.JsonObject;

public final class FlatnessCheckDataSerializer
{
	private static final String OVERRIDE_GLOBAL_FLATNESS_CHECK_PROPERTY = "override_global_flatness_check";
	private static final String ENABLE_FLATNESS_CHECK_PROPERTY = "enable_flatness_check";
	private static final String FLATNESS_CHECK_ALLOW_NON_SOLID_PROPERTY = "flatness_check_allow_non_solid_blocks";
	private static final String FLATNESS_CHECK_MODE_PROPERTY = "flatness_check_mode";
	private static final String FLATNESS_CHECK_MAX_HEIGHT_DIFFERENCE = "flatness_check_max_height_difference";

	public static void load(JsonObject structureJson, FlatnessCheckData flatnessCheckData) {
		if (structureJson.has(OVERRIDE_GLOBAL_FLATNESS_CHECK_PROPERTY)) {
			var overrideGlobalFlatnessCheck = structureJson.get(OVERRIDE_GLOBAL_FLATNESS_CHECK_PROPERTY).getAsBoolean();
			flatnessCheckData.overrideGlobalFlatnessCheck(overrideGlobalFlatnessCheck);
		}

		if (structureJson.has(ENABLE_FLATNESS_CHECK_PROPERTY)) {
			var isEnabled = structureJson.get(ENABLE_FLATNESS_CHECK_PROPERTY).getAsBoolean();
			flatnessCheckData.enable(isEnabled);
		}

		if (structureJson.has(FLATNESS_CHECK_ALLOW_NON_SOLID_PROPERTY)) {
			var allowNonSolidBlocks = structureJson.get(FLATNESS_CHECK_ALLOW_NON_SOLID_PROPERTY).getAsBoolean();
			flatnessCheckData.allowNonSolidBlocks(allowNonSolidBlocks);
		}

		if (structureJson.has(FLATNESS_CHECK_MODE_PROPERTY)) {
			var flatnessCheckMode = structureJson.get(FLATNESS_CHECK_MODE_PROPERTY).getAsString();
			flatnessCheckData.setMode(FlatnessCheckData.FlatnessCheckMode.valueOf(flatnessCheckMode));
		}

		if (structureJson.has(FLATNESS_CHECK_MAX_HEIGHT_DIFFERENCE)) {
			var maxHeightDifference = structureJson.get(FLATNESS_CHECK_MAX_HEIGHT_DIFFERENCE).getAsInt();
			flatnessCheckData.setMaxHeightDifference(maxHeightDifference);
		}
	}

	public static void save(JsonObject structureJson, FlatnessCheckData flatnessCheckData) {
		structureJson.addProperty(OVERRIDE_GLOBAL_FLATNESS_CHECK_PROPERTY, flatnessCheckData.isOverridingGlobalFlatnessCheck());
		structureJson.addProperty(ENABLE_FLATNESS_CHECK_PROPERTY, flatnessCheckData.isEnabled());
		structureJson.addProperty(FLATNESS_CHECK_ALLOW_NON_SOLID_PROPERTY, flatnessCheckData.areNonSolidBlocksAllowed());
		structureJson.addProperty(FLATNESS_CHECK_MODE_PROPERTY, flatnessCheckData.getMode().name());
		structureJson.addProperty(FLATNESS_CHECK_MAX_HEIGHT_DIFFERENCE, flatnessCheckData.getMaxHeightDifference());
	}
}
