//? if lithostitched {
package com.faboslav.structurify.common.modcompat;

import com.faboslav.structurify.common.config.data.structure.JigsawData;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.IntProvider;
//? if >= 26.1 {
import net.minecraft.util.valueproviders.IntProviders;
//?}
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import org.jetbrains.annotations.Nullable;

//? if >= 26.3 {
import dev.worldgen.lithostitched.impl.worldgen.structure.AlternateJigsawConfig;
//?} else {
//import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawConfig;
//?}

import java.util.Optional;

public final class LithostitchedCompat
{
	private static final String SIZE_PROPERTY = "size";
	private static final String START_HEIGHT_PROPERTY = "start_height";
	private static final String MAX_DISTANCE_FROM_CENTER_PROPERTY = "max_distance_from_center";
	private static final String HORIZONTAL_PROPERTY = "horizontal";
	private static final String VERTICAL_PROPERTY = "vertical";
	private static final String PROJECT_START_TO_HEIGHTMAP_PROPERTY = "project_start_to_heightmap";
	private static final String START_PROJECTION_PROPERTY = "start_projection";

	private LithostitchedCompat() {
	}

	@Nullable
	public static Integer getSize(@Nullable JsonObject structureJson) {
		var serializationContext = StructurifyRegistryManagerProvider.getSerializationContext();

		if (serializationContext == null || structureJson == null || !structureJson.has(SIZE_PROPERTY)) {
			return null;
		}

		//? if >= 26.1 {
		return IntProviders.CODEC
		//?} else {
		//return IntProvider.CODEC
		//?}
			.parse(serializationContext, structureJson.get(SIZE_PROPERTY))
			.result()
			//? if >= 26.1 {
			.map(IntProvider::maxInclusive)
			//?} else {
			//.map(IntProvider::getMaxValue)
			//?}
			.orElse(null);
	}

	@Nullable
	public static Optional<Heightmap.Types> getProjectStartToHeightmap(@Nullable JsonObject structureJson) {
		var serializationContext = StructurifyRegistryManagerProvider.getSerializationContext();

		if (serializationContext == null || structureJson == null) {
			return null;
		}

		JsonElement projection = structureJson.get(START_PROJECTION_PROPERTY);

		if (projection == null) {
			projection = structureJson.get(PROJECT_START_TO_HEIGHTMAP_PROPERTY);
		}

		if (projection == null || projection.isJsonNull()) {
			return Optional.empty();
		}

		return Heightmap.Types.CODEC
			.parse(serializationContext, projection)
			.result()
			.map(Optional::of)
			.orElse(null);
	}

	@Nullable
	public static AlternateJigsawConfig getModifiedConfig(AlternateJigsawConfig originalConfig, JigsawData jigsawData) {
		if (jigsawData.isUsingDefaultValues()) {
			return originalConfig;
		}

		var serializationContext = StructurifyRegistryManagerProvider.getSerializationContext();

		if (serializationContext == null) {
			return null;
		}

		Codec<AlternateJigsawConfig> codec = AlternateJigsawConfig.CODEC.codec();
		var serializedConfig = codec.encodeStart(serializationContext, originalConfig)
			.result()
			.orElse(null);

		if (serializedConfig == null || !serializedConfig.isJsonObject()) {
			return null;
		}

		var configData = serializedConfig.getAsJsonObject().deepCopy();

		if (!jigsawData.isUsingDefaultSize() && jigsawData.getSize() != null) {
			configData.addProperty(SIZE_PROPERTY, jigsawData.getSize());
		}

		if (!jigsawData.isUsingDefaultHeightProvider() && jigsawData.getHeightProviderData() != null) {
			HeightProvider.CODEC
				.encodeStart(serializationContext, jigsawData.getHeightProviderData().toHeightProvider())
				.result()
				.ifPresent(startHeight -> configData.add(START_HEIGHT_PROPERTY, startHeight));
		}

		if (!jigsawData.isUsingDefaultProjectStartToHeightmap() && jigsawData.getProjectStartToHeightmap() != null) {
			configData.remove(PROJECT_START_TO_HEIGHTMAP_PROPERTY);
			configData.remove(START_PROJECTION_PROPERTY);

			jigsawData.getProjectStartToHeightmap().toDataValue().ifPresent(heightmap -> {
				Heightmap.Types.CODEC
					.encodeStart(serializationContext, heightmap)
					.result()
					.ifPresent(projection -> {
						configData.add(PROJECT_START_TO_HEIGHTMAP_PROPERTY, projection);
						configData.add(START_PROJECTION_PROPERTY, projection.deepCopy());
					});
			});
		}

		var horizontalMaxDistanceFromCenter = jigsawData.getHorizontalMaxDistanceFromCenter();
		var verticalMaxDistanceFromCenter = jigsawData.getVerticalMaxDistanceFromCenter();
		var modifyMaxDistanceFromCenter = !jigsawData.isUsingDefaultMaxDistanceFromCenter() && horizontalMaxDistanceFromCenter != null;

		if (modifyMaxDistanceFromCenter) {
			var maxDistanceFromCenter = new JsonObject();
			maxDistanceFromCenter.addProperty(HORIZONTAL_PROPERTY, horizontalMaxDistanceFromCenter);
			maxDistanceFromCenter.addProperty(VERTICAL_PROPERTY, verticalMaxDistanceFromCenter != null ? verticalMaxDistanceFromCenter : horizontalMaxDistanceFromCenter);
			configData.add(MAX_DISTANCE_FROM_CENTER_PROPERTY, maxDistanceFromCenter);
		}

		var modifiedConfig = codec.parse(serializationContext, configData).result();

		if (modifiedConfig.isEmpty() && modifyMaxDistanceFromCenter) {
			configData.addProperty(MAX_DISTANCE_FROM_CENTER_PROPERTY, horizontalMaxDistanceFromCenter);
			modifiedConfig = codec.parse(serializationContext, configData).result();
		}

		return modifiedConfig.orElse(null);
	}
}
//?}
