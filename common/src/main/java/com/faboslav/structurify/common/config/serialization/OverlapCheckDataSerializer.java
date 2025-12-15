package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.config.data.structure.OverlapCheckData;
import com.google.gson.JsonObject;

public final class OverlapCheckDataSerializer
{
	private static final String EXCLUDE_FROM_OVERLAP_PREVENTION_PROPERTY = "exclude_from_overlap_prevention";

	public static void load(JsonObject structureJson, OverlapCheckData overlapCheckData) {
		if (structureJson.has(EXCLUDE_FROM_OVERLAP_PREVENTION_PROPERTY)) {
			var excludeFromOverlapPrevention = structureJson.get(EXCLUDE_FROM_OVERLAP_PREVENTION_PROPERTY).getAsBoolean();
			overlapCheckData.excludeFromOverlapPrevention(excludeFromOverlapPrevention);
		}
	}

	public static void save(JsonObject structureJson, OverlapCheckData overlapCheckData) {
		structureJson.addProperty(EXCLUDE_FROM_OVERLAP_PREVENTION_PROPERTY, overlapCheckData.isExcludedFromOverlapPrevention());
	}
}
