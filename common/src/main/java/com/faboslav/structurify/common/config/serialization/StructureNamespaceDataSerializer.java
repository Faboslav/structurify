package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class StructureNamespaceDataSerializer
{
	public static final String NAME_PROPERTY = "name";
	private static final String IS_DISABLED_PROPERTY = "is_disabled";

	public static void load(JsonObject structureJson, StructureNamespaceData structureNamespaceData) {
		if (structureJson.has(IS_DISABLED_PROPERTY)) {
			structureNamespaceData.setDisabled(structureJson.get(IS_DISABLED_PROPERTY).getAsBoolean());
		}

		DistanceFromWorldCenterDataSerializer.load(structureJson, structureNamespaceData.getDistanceFromWorldCenterCheckData());
		OverlapCheckDataSerializer.load(structureJson, structureNamespaceData.getOverlapCheckData());
		FlatnessCheckDataSerializer.load(structureJson, structureNamespaceData.getFlatnessCheckData());
		BiomeCheckDataSerializer.load(structureJson, structureNamespaceData.getBiomeCheckData());
	}

	public static void save(
		JsonArray structureNamespacesJson,
		String structureName,
		StructureNamespaceData structureNamespaceData,
		boolean saveOnlyChanged
	) {
		JsonObject structureNamespace = new JsonObject();
		structureNamespace.addProperty(NAME_PROPERTY, structureName);

		if (!structureNamespaceData.isUsingDefaultIsDisabled() || !saveOnlyChanged) {
			structureNamespace.addProperty(IS_DISABLED_PROPERTY, structureNamespaceData.isDisabled());
		}

		var distanceFromWorldCenterData = structureNamespaceData.getDistanceFromWorldCenterCheckData();
		if (!distanceFromWorldCenterData.isUsingDefaultValues() || !saveOnlyChanged) {
			DistanceFromWorldCenterDataSerializer.save(structureNamespace, distanceFromWorldCenterData, saveOnlyChanged);
		}

		var overlapCheckData = structureNamespaceData.getOverlapCheckData();
		if(!overlapCheckData.isUsingDefaultValues() || !saveOnlyChanged) {
			OverlapCheckDataSerializer.save(structureNamespace, overlapCheckData, saveOnlyChanged);
		}

		var flatnessCheckData = structureNamespaceData.getFlatnessCheckData();
		if (!flatnessCheckData.isUsingDefaultValues() || !saveOnlyChanged) {
			FlatnessCheckDataSerializer.save(structureNamespace, flatnessCheckData, saveOnlyChanged);
		}

		var biomeCheckData = structureNamespaceData.getBiomeCheckData();
		if (!biomeCheckData.isUsingDefaultValues() || !saveOnlyChanged) {
			BiomeCheckDataSerializer.save(structureNamespace, biomeCheckData, saveOnlyChanged);
		}

		structureNamespacesJson.add(structureNamespace);
	}
}
