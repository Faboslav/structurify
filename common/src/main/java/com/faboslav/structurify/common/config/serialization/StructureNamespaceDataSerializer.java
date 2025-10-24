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
		FlatnessCheckDataSerializer.load(structureJson, structureNamespaceData.getFlatnessCheckData());
		BiomeCheckDataSerializer.load(structureJson, structureNamespaceData.getBiomeCheckData());
	}

	public static void save(
		JsonArray structureNamespacesJson,
		String structureName,
		StructureNamespaceData structureNamespaceData
	) {
		JsonObject structureNamespace = new JsonObject();
		structureNamespace.addProperty(NAME_PROPERTY, structureName);
		structureNamespace.addProperty(IS_DISABLED_PROPERTY, structureNamespaceData.isDisabled());

		var distanceFromWorldCenterData = structureNamespaceData.getDistanceFromWorldCenterCheckData();
		if (!distanceFromWorldCenterData.isUsingDefaultValues()) {
			DistanceFromWorldCenterDataSerializer.save(structureNamespace, distanceFromWorldCenterData);
		}

		var flatnessCheckData = structureNamespaceData.getFlatnessCheckData();
		if (!flatnessCheckData.isUsingDefaultValues()) {
			FlatnessCheckDataSerializer.save(structureNamespace, flatnessCheckData);
		}

		var biomeCheckData = structureNamespaceData.getBiomeCheckData();
		if (!biomeCheckData.isUsingDefaultValues()) {
			BiomeCheckDataSerializer.save(structureNamespace, biomeCheckData);
		}

		structureNamespacesJson.add(structureNamespace);
	}
}
