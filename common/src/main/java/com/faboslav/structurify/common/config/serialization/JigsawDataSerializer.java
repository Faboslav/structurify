package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.config.data.structure.JigsawData;
import com.google.gson.JsonObject;

public final class JigsawDataSerializer
{
	private static final String SIZE_PROPERTY = "max_distance_from_center";
	private static final String MAX_HORIZONTAL_DISTANCE_FROM_CENTER_PROPERTY = "max_horizontal_distance_from_center";
	private static final String MAX_VERTICAL_DISTANCE_FROM_CENTER_PROPERTY = "max_vertical_distance_from_center";

	public static void load(JsonObject structureJson, JigsawData jigsawData) {
		if (structureJson.has(SIZE_PROPERTY)) {
			var size = structureJson.get(SIZE_PROPERTY).getAsInt();
			jigsawData.setSize(size);
		}

		if (structureJson.has(MAX_HORIZONTAL_DISTANCE_FROM_CENTER_PROPERTY) && structureJson.has(MAX_HORIZONTAL_DISTANCE_FROM_CENTER_PROPERTY)) {
			var maxHorizontalDistanceFromCenter = structureJson.get(MAX_HORIZONTAL_DISTANCE_FROM_CENTER_PROPERTY).getAsInt();
			jigsawData.setHorizontalMaxDistanceFromCenter(maxHorizontalDistanceFromCenter);

			var maxVerticalDistanceFromCenter = structureJson.get(MAX_VERTICAL_DISTANCE_FROM_CENTER_PROPERTY).getAsInt();
			jigsawData.setVerticalMaxDistanceFromCenter(maxVerticalDistanceFromCenter);
		}
	}

	public static void save(JsonObject structureJson, JigsawData jigsawData) {
		structureJson.addProperty(SIZE_PROPERTY, jigsawData.getSize());
		structureJson.addProperty(MAX_VERTICAL_DISTANCE_FROM_CENTER_PROPERTY, jigsawData.getHorizontalMaxDistanceFromCenter());
		structureJson.addProperty(MAX_VERTICAL_DISTANCE_FROM_CENTER_PROPERTY, jigsawData.getVerticalMaxDistanceFromCenter());
	}
}
