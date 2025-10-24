package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.google.gson.JsonObject;

public final class FlatnessCheckDataSerializer
{
	private static final String OVERRIDE_GLOBAL_FLATNESS_CHECK_PROPERTY = "override_global_flatness_check";
	private static final String ENABLE_FLATNESS_CHECK_PROPERTY = "enable_flatness_check";
	private static final String FLATNESS_CHECK_ALLOW_NON_SOLID_PROPERTY = "flatness_check_allow_non_solid_blocks";

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
	}

	public static void save(JsonObject structureJson, FlatnessCheckData flatnessCheckData) {
		structureJson.addProperty(OVERRIDE_GLOBAL_FLATNESS_CHECK_PROPERTY, flatnessCheckData.isOverridingGlobalFlatnessCheck());
		structureJson.addProperty(ENABLE_FLATNESS_CHECK_PROPERTY, flatnessCheckData.isEnabled());
		structureJson.addProperty(FLATNESS_CHECK_ALLOW_NON_SOLID_PROPERTY, flatnessCheckData.areNonSolidBlocksAllowed());
	}
}
