package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.structure.JigsawData;
import com.faboslav.structurify.common.config.data.structure.jigsaw.HeightProviderData;
import com.faboslav.structurify.common.config.data.structure.jigsaw.ProjectStartToHeightmap;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public final class JigsawDataSerializer
{
	private static final String SIZE_PROPERTY = "size";
	private static final String MAX_HORIZONTAL_DISTANCE_FROM_CENTER_PROPERTY = "max_horizontal_distance_from_center";
	private static final String MAX_VERTICAL_DISTANCE_FROM_CENTER_PROPERTY = "max_vertical_distance_from_center";
	private static final String START_HEIGHT_PROPERTY = "start_height";
	private static final String PROJECT_START_TO_HEIGHTMAP_PROPERTY = "project_start_to_heightmap";

	public static void load(JsonObject structureJson, JigsawData jigsawData, String structureName) {
		if (structureJson.has(SIZE_PROPERTY)) {
			var size = structureJson.get(SIZE_PROPERTY).getAsInt();
			jigsawData.setSize(size);
		}

		if (structureJson.has(MAX_HORIZONTAL_DISTANCE_FROM_CENTER_PROPERTY) && structureJson.has(MAX_VERTICAL_DISTANCE_FROM_CENTER_PROPERTY)) {
			var maxHorizontalDistanceFromCenter = structureJson.get(MAX_HORIZONTAL_DISTANCE_FROM_CENTER_PROPERTY).getAsInt();
			jigsawData.setHorizontalMaxDistanceFromCenter(maxHorizontalDistanceFromCenter);

			var maxVerticalDistanceFromCenter = structureJson.get(MAX_VERTICAL_DISTANCE_FROM_CENTER_PROPERTY).getAsInt();
			jigsawData.setVerticalMaxDistanceFromCenter(maxVerticalDistanceFromCenter);
		}

		if (structureJson.has(START_HEIGHT_PROPERTY)) {
			var heightProvider = HeightProvider.CODEC
				.parse(JsonOps.INSTANCE, structureJson.get("start_height"))
				.result()
				.orElse(null);

			if(heightProvider != null) {
				var heightProviderData = HeightProviderData.fromHeightProvider(heightProvider);
				jigsawData.setHeightProviderData(heightProviderData);
			}
		}

		if (jigsawData.getProjectStartToHeightmap() != null && structureJson.has(PROJECT_START_TO_HEIGHTMAP_PROPERTY)) {
			String rawProjectStartToHeightmap = structureJson.get(PROJECT_START_TO_HEIGHTMAP_PROPERTY).getAsString();

			ProjectStartToHeightmap projectStartToHeightmap = Arrays.stream(Heightmap.Types.values())
				.filter(s -> s.getSerializedName().equals(rawProjectStartToHeightmap))
				.findFirst()
				.map(types -> ProjectStartToHeightmap.fromDataValue(Optional.of(types)))
				.orElseGet(() -> {
					Structurify.getLogger().info(
						"ProjectStartToHeightmap value for structure {} is currently '{}', which is invalid. Value will be corrected to none.",
						structureName,
						rawProjectStartToHeightmap
					);
					return ProjectStartToHeightmap.NONE;
				});

			jigsawData.setProjectStartToHeightmap(projectStartToHeightmap);
		}
	}

	public static void save(JsonObject structureJson, JigsawData jigsawData) {
		var size = jigsawData.getSize();

		if(size != null && !jigsawData.isUsingDefaultSize()) {
			structureJson.addProperty(SIZE_PROPERTY, jigsawData.getSize());
		}

		var horizontalMaxDistanceFromCenter = jigsawData.getHorizontalMaxDistanceFromCenter();
		var verticalMaxDistanceFromCenter = jigsawData.getVerticalMaxDistanceFromCenter();

		if(horizontalMaxDistanceFromCenter != null && verticalMaxDistanceFromCenter != null && !jigsawData.isUsingDefaultMaxDistanceFromCenter()) {
			structureJson.addProperty(MAX_HORIZONTAL_DISTANCE_FROM_CENTER_PROPERTY, horizontalMaxDistanceFromCenter);
			structureJson.addProperty(MAX_VERTICAL_DISTANCE_FROM_CENTER_PROPERTY, verticalMaxDistanceFromCenter);
		}

		var heightProviderData = jigsawData.getHeightProviderData();

		if(heightProviderData != null && !jigsawData.isUsingDefaultHeightProvider()) {
			var heightProvider = heightProviderData.toHeightProvider();

			var heightProviderJson = HeightProvider.CODEC
				.encodeStart(JsonOps.INSTANCE, heightProvider)
				.result().orElse(null);

			structureJson.add(START_HEIGHT_PROPERTY, heightProviderJson);
		}

		var projectStartToHeightmap = jigsawData.getProjectStartToHeightmap();

		if (projectStartToHeightmap != null && !jigsawData.isUsingDefaultProjectStartToHeightmap()) {
			var projectStartToHeightmapValue = projectStartToHeightmap.toDataValue();

			if (projectStartToHeightmapValue.isPresent()) {
				structureJson.addProperty(PROJECT_START_TO_HEIGHTMAP_PROPERTY, projectStartToHeightmapValue.get().getSerializedName());
			} else {
				structureJson.addProperty(PROJECT_START_TO_HEIGHTMAP_PROPERTY, projectStartToHeightmap.name());
			}
		}
	}
}
