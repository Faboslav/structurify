package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.config.data.structure.OverlapCheckData;
import com.google.gson.JsonObject;

public final class OverlapCheckDataSerializer
{
	private static final String EXCLUDE_FROM_OVERLAP_PREVENTION_PROPERTY = "exclude_from_overlap_prevention";
	private static final String OVERRIDE_GLOBAL_OVERLAP_PADDING_PROPERTY = "override_global_overlap_padding";
	private static final String OVERLAP_PADDING_PROPERTY = "overlap_padding";

	public static void load(JsonObject structureJson, OverlapCheckData overlapCheckData) {
		if (structureJson.has(EXCLUDE_FROM_OVERLAP_PREVENTION_PROPERTY)) {
			var excludeFromOverlapPrevention = structureJson.get(EXCLUDE_FROM_OVERLAP_PREVENTION_PROPERTY).getAsBoolean();
			overlapCheckData.excludeFromOverlapPrevention(excludeFromOverlapPrevention);
		}

		if (structureJson.has(OVERRIDE_GLOBAL_OVERLAP_PADDING_PROPERTY)) {
			var overrideGlobalOverlapPadding = structureJson.get(OVERRIDE_GLOBAL_OVERLAP_PADDING_PROPERTY).getAsBoolean();
			overlapCheckData.overrideGlobalOverlapPadding(overrideGlobalOverlapPadding);
		}

		if (structureJson.has(OVERLAP_PADDING_PROPERTY)) {
			var overlapPadding = structureJson.get(OVERLAP_PADDING_PROPERTY).getAsInt();
			overlapCheckData.setOverlapPadding(overlapPadding);
		}
	}

	public static void save(JsonObject structureJson, OverlapCheckData overlapCheckData, boolean saveOnlyChanged) {
		if (!overlapCheckData.isUsingDefaultIsExcludedFromOverlapPrevention() || !saveOnlyChanged) {
			structureJson.addProperty(EXCLUDE_FROM_OVERLAP_PREVENTION_PROPERTY, overlapCheckData.isExcludedFromOverlapPrevention());
		}

		if (!overlapCheckData.isUsingDefaultOverrideGlobalOverlapPadding() || !saveOnlyChanged) {
			structureJson.addProperty(OVERRIDE_GLOBAL_OVERLAP_PADDING_PROPERTY, overlapCheckData.isOverridingGlobalOverlapPadding());
		}

		if (!overlapCheckData.isUsingDefaultOverlapPadding() || !saveOnlyChanged) {
			structureJson.addProperty(OVERLAP_PADDING_PROPERTY, overlapCheckData.getOverlapPadding());
		}
	}
}
