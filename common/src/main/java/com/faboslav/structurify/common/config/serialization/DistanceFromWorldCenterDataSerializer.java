package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.config.data.structure.DistanceFromWorldCenterCheckData;
import com.google.gson.JsonObject;

public class DistanceFromWorldCenterDataSerializer
{
	private static final String MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY = "min_distance_from_world_center";
	private static final String MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY = "max_distance_from_world_center";

	public static void load(JsonObject structureJson, DistanceFromWorldCenterCheckData distanceFromWorldCenterData) {
		if (structureJson.has(MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY)) {
			var minDistanceFromWorldCenter = structureJson.get(MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY).getAsInt();
			distanceFromWorldCenterData.setMinDistanceFromWorldCenter(minDistanceFromWorldCenter);
		}

		if (structureJson.has(MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY)) {
			var maxDistanceFromWorldCenter = structureJson.get(MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY).getAsInt();
			distanceFromWorldCenterData.setMaxDistanceFromWorldCenter(maxDistanceFromWorldCenter);
		}
	}

	public static void save(JsonObject structureJson, DistanceFromWorldCenterCheckData distanceFromWorldCenterData) {
		structureJson.addProperty(MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY, distanceFromWorldCenterData.getMinDistanceFromWorldCenter());
		structureJson.addProperty(MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY, distanceFromWorldCenterData.getMaxDistanceFromWorldCenter());
	}
}
