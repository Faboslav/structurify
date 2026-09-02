package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.config.data.structure.DistanceFromWorldCenterCheckData;
import com.google.gson.JsonObject;

public class DistanceFromWorldCenterDataSerializer
{
	private static final String OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_PROPERTY = "override_global_distance_from_world_center";
	private static final String DISTANCE_FROM_WORLD_CENTER_MODE_PROPERTY = "distance_from_world_center_mode";
	private static final String ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY = "enable_min_distance_from_world_center";
	private static final String ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY = "enable_max_distance_from_world_center";
	private static final String MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY = "min_distance_from_world_center";
	private static final String MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY = "max_distance_from_world_center";

	public static void load(JsonObject structureJson, DistanceFromWorldCenterCheckData distanceFromWorldCenterData) {
		if (structureJson.has(OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_PROPERTY)) {
			var overrideGlobalDistanceFromWorldCenter = structureJson.get(OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_PROPERTY).getAsBoolean();
			distanceFromWorldCenterData.overrideGlobalDistanceFromWorldCenter(overrideGlobalDistanceFromWorldCenter);
		}

		if (structureJson.has(DISTANCE_FROM_WORLD_CENTER_MODE_PROPERTY)) {
			var distanceFromWorldCenterMode = structureJson.get(DISTANCE_FROM_WORLD_CENTER_MODE_PROPERTY).getAsString();
			distanceFromWorldCenterData.setMode(DistanceFromWorldCenterCheckData.DistanceFromWorldCenterCheckMode.valueOf(distanceFromWorldCenterMode));
		}

		if (structureJson.has(ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY)) {
			var enableMinDistanceFromWorldCenter = structureJson.get(ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY).getAsBoolean();
			distanceFromWorldCenterData.enableMinDistanceFromWorldCenter(enableMinDistanceFromWorldCenter);
		}

		if (structureJson.has(MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY)) {
			var minDistanceFromWorldCenter = structureJson.get(MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY).getAsInt();
			distanceFromWorldCenterData.setMinDistanceFromWorldCenter(minDistanceFromWorldCenter);

			if (!structureJson.has(ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY) && minDistanceFromWorldCenter != DistanceFromWorldCenterCheckData.MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE) {
				distanceFromWorldCenterData.enableMinDistanceFromWorldCenter(true);
			}
		}

		if (structureJson.has(ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY)) {
			var enableMaxDistanceFromWorldCenter = structureJson.get(ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY).getAsBoolean();
			distanceFromWorldCenterData.enableMaxDistanceFromWorldCenter(enableMaxDistanceFromWorldCenter);
		}

		if (structureJson.has(MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY)) {
			var maxDistanceFromWorldCenter = structureJson.get(MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY).getAsInt();
			distanceFromWorldCenterData.setMaxDistanceFromWorldCenter(maxDistanceFromWorldCenter);

			if (!structureJson.has(ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY) && maxDistanceFromWorldCenter != DistanceFromWorldCenterCheckData.MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE) {
				distanceFromWorldCenterData.enableMaxDistanceFromWorldCenter(true);
			}
		}
	}

	public static void save(JsonObject structureJson, DistanceFromWorldCenterCheckData distanceFromWorldCenterData, boolean saveOnlyChanged) {
		if (!distanceFromWorldCenterData.isUsingDefaultOverrideGlobalDistanceFromWorldCenter() || !saveOnlyChanged) {
			structureJson.addProperty(OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_PROPERTY, distanceFromWorldCenterData.isOverridingGlobalDistanceFromWorldCenter());
		}

		if (!distanceFromWorldCenterData.isUsingDefaultMode() || !saveOnlyChanged) {
			structureJson.addProperty(DISTANCE_FROM_WORLD_CENTER_MODE_PROPERTY, distanceFromWorldCenterData.getMode().name());
		}

		if (!distanceFromWorldCenterData.isUsingDefaultEnableMinDistanceFromWorldCenter() || !saveOnlyChanged) {
			structureJson.addProperty(ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY, distanceFromWorldCenterData.isMinDistanceFromWorldCenterEnabled());
		}

		if (!distanceFromWorldCenterData.isUsingDefaultMinDistanceFromWorldCenter() || !saveOnlyChanged) {
			structureJson.addProperty(MIN_DISTANCE_FROM_WORLD_CENTER_PROPERTY, distanceFromWorldCenterData.getMinDistanceFromWorldCenter());
		}

		if (!distanceFromWorldCenterData.isUsingDefaultEnableMaxDistanceFromWorldCenter() || !saveOnlyChanged) {
			structureJson.addProperty(ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY, distanceFromWorldCenterData.isMaxDistanceFromWorldCenterEnabled());
		}

		if (!distanceFromWorldCenterData.isUsingDefaultMaxDistanceFromWorldCenter() || !saveOnlyChanged) {
			structureJson.addProperty(MAX_DISTANCE_FROM_WORLD_CENTER_PROPERTY, distanceFromWorldCenterData.getMaxDistanceFromWorldCenter());
		}
	}
}
