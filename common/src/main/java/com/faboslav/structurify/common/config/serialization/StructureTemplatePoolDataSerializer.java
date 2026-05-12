package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureTemplatePoolData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public final class StructureTemplatePoolDataSerializer
{
	public static final String NAME_PROPERTY = "name";
	private static final String ELEMENT_WEIGHTS_PROPERTY = "element_weights";

	public static void load(JsonObject structureSetJson, StructureTemplatePoolData structureTemplatePoolData) {
		var structureTemplatePoolName = structureSetJson.get(NAME_PROPERTY).getAsString();

		if(structureSetJson.has(ELEMENT_WEIGHTS_PROPERTY)) {
			var structureWeights = structureSetJson.get(ELEMENT_WEIGHTS_PROPERTY).getAsJsonObject();

			for (Map.Entry<String, JsonElement> structureTemplatePoolElementWeightEntry : structureWeights.entrySet()) {
				String structureTemplatePoolElementId = structureTemplatePoolElementWeightEntry.getKey();
				JsonElement structureTemplatePoolElementWeight = structureTemplatePoolElementWeightEntry.getValue();

				if (!structureTemplatePoolElementWeight.isJsonPrimitive() || !structureTemplatePoolElementWeight.getAsJsonPrimitive().isNumber()) {
					continue;
				}

				int weight = structureTemplatePoolElementWeight.getAsInt();

				if (weight < StructureTemplatePoolData.MIN_STRUCTURE_TEMPLATE_POOL_ELEMENT_WEIGHT) {
					Structurify.getLogger().info("Structure weight value for structure set {} is currently {}, which is lower than minimum value of {}, value will be automatically corrected to 1.", structureTemplatePoolName, weight, StructureTemplatePoolData.MIN_STRUCTURE_TEMPLATE_POOL_ELEMENT_WEIGHT);
					weight = 1;
				}

				structureTemplatePoolData.getStructureTemplatePoolElementWeights().put(structureTemplatePoolElementId, weight);
			}
		}
	}

	public static void save(JsonArray structureSetsJson, String structureTemplatePoolName, StructureTemplatePoolData structureTemplatePoolData) {
		JsonObject structureTemplatePool = new JsonObject();
		JsonObject structureTemplatePoolElementWeights = new JsonObject();

		for(var structureTemplatePoolElementWeightEntry : structureTemplatePoolData.getStructureTemplatePoolElementWeights().entrySet()) {
			var structureTemplatePoolElementName = structureTemplatePoolElementWeightEntry.getKey();
			var defaultStructurePoolElementWeight = structureTemplatePoolData.getDefaultStructureTemplatePoolElementWeights().get(structureTemplatePoolElementName);
			var structureTemplatePoolElementWeight = structureTemplatePoolElementWeightEntry.getValue();

			if(defaultStructurePoolElementWeight.equals(structureTemplatePoolElementWeight)) {
				continue;
			}

			structureTemplatePoolElementWeights.addProperty(structureTemplatePoolElementName, structureTemplatePoolElementWeight);
		}

		structureTemplatePool.addProperty(NAME_PROPERTY, structureTemplatePoolName);
		structureTemplatePool.add(ELEMENT_WEIGHTS_PROPERTY, structureTemplatePoolElementWeights);

		structureSetsJson.add(structureTemplatePool);
	}
}
