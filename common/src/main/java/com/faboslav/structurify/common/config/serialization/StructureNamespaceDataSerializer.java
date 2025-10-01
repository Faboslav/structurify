package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class StructureNamespaceDataSerializer {
	public static final String NAME_PROPERTY = "name";
	private static final String IS_DISABLED_PROPERTY = "is_disabled";

	public static void load(JsonObject structureJson, StructureNamespaceData structureNamespaceData) {
		if (structureJson.has(IS_DISABLED_PROPERTY)) {
			structureNamespaceData.setDisabled(structureJson.get(IS_DISABLED_PROPERTY).getAsBoolean());
		}

		FlatnessCheckDataSerializer.load(structureJson, structureNamespaceData.getFlatnessCheckData());
		BiomeCheckDataSerializer.load(structureJson, structureNamespaceData.getBiomeCheckData());
	}

	public static void save(JsonArray structureNamespacesJson, String structureName, StructureNamespaceData structureNamespaceData) {
		JsonObject structureNamespace = new JsonObject();
		structureNamespace.addProperty(NAME_PROPERTY, structureName);
		structureNamespace.addProperty(IS_DISABLED_PROPERTY, structureNamespaceData.isDisabled());
		FlatnessCheckDataSerializer.save(structureNamespace, structureNamespaceData.getFlatnessCheckData());
		BiomeCheckDataSerializer.save(structureNamespace, structureNamespaceData.getBiomeCheckData());

		structureNamespacesJson.add(structureNamespace);
	}
}
