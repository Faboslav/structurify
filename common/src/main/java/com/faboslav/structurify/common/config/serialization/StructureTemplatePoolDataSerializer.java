package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureTemplatePoolData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Mth;

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

				if (!structureTemplatePoolData.getDefaultStructureTemplatePoolElementWeights().containsKey(structureTemplatePoolElementId)) {
					Structurify.getLogger().info("Found invalid structure template pool element identifier of \"{}\" in the weights of the {} structure template pool, skipping.", structureTemplatePoolElementId, structureTemplatePoolName);
					continue;
				}

				if (!structureTemplatePoolElementWeight.isJsonPrimitive() || !structureTemplatePoolElementWeight.getAsJsonPrimitive().isNumber()) {
					continue;
				}

				int weight = structureTemplatePoolElementWeight.getAsInt();

				if (weight < StructureTemplatePoolData.MIN_STRUCTURE_TEMPLATE_POOL_ELEMENT_WEIGHT || weight > StructureTemplatePoolData.MAX_STRUCTURE_TEMPLATE_POOL_ELEMENT_WEIGHT) {
					int correctedWeight = Mth.clamp(weight, StructureTemplatePoolData.MIN_STRUCTURE_TEMPLATE_POOL_ELEMENT_WEIGHT, StructureTemplatePoolData.MAX_STRUCTURE_TEMPLATE_POOL_ELEMENT_WEIGHT);
					Structurify.getLogger().info("Element weight value of {} for structure template pool {} is currently {}, which is outside of the range of {} to {}, value will be automatically corrected to {}.", structureTemplatePoolElementId, structureTemplatePoolName, weight, StructureTemplatePoolData.MIN_STRUCTURE_TEMPLATE_POOL_ELEMENT_WEIGHT, StructureTemplatePoolData.MAX_STRUCTURE_TEMPLATE_POOL_ELEMENT_WEIGHT, correctedWeight);
					weight = correctedWeight;
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

			if(defaultStructurePoolElementWeight == null || defaultStructurePoolElementWeight.equals(structureTemplatePoolElementWeight)) {
				continue;
			}

			structureTemplatePoolElementWeights.addProperty(structureTemplatePoolElementName, structureTemplatePoolElementWeight);
		}

		structureTemplatePool.addProperty(NAME_PROPERTY, structureTemplatePoolName);
		structureTemplatePool.add(ELEMENT_WEIGHTS_PROPERTY, structureTemplatePoolElementWeights);

		structureSetsJson.add(structureTemplatePool);
	}
}
