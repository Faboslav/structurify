package com.faboslav.structurify.common.util;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.mixin.structure.jigsaw.JigsawStructureAccessor;
import com.faboslav.structurify.common.platform.PlatformHooks;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

//? if yungs_api {
/*import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
*///?}

//? if repurposed_structures {
/*import com.telepathicgrunt.repurposedstructures.world.structures.GenericJigsawStructure;
*///?}

//? if yungs_api || repurposed_structures {
/*import com.faboslav.structurify.common.platform.PlatformHooks;
*///?}

public final class JigsawStructureUtil
{
	@Nullable
	public static JsonObject getStructureData(Structure structure) {
		var serializationContext = StructurifyRegistryManagerProvider.getSerializationContext();

		if (serializationContext == null) {
			return null;
		}

		try {
			return Structure.DIRECT_CODEC
				.encodeStart(serializationContext, structure)
				.result()
				.filter(JsonElement::isJsonObject)
				.map(JsonElement::getAsJsonObject)
				.orElseThrow();
		} catch(Exception exception) {
			try {
				//? if >= 1.21.1 {
				return JigsawStructure.CODEC.codec()
				//?} else {
				/*return JigsawStructure.CODEC
				*///?}
					.encodeStart(serializationContext, (JigsawStructure) structure)
					.result()
					.filter(JsonElement::isJsonObject)
					.map(JsonElement::getAsJsonObject)
					.orElseThrow();
			} catch (Exception ignored) {
			}

			return null;
		}
	}

	public static boolean isJigsawLikeStructure(Structure structure, @Nullable JsonObject structureJson) {
		if (structure instanceof JigsawStructure) {
			return true;
		}

		//? if yungs_api {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("yungsapi") && structure instanceof YungJigsawStructure) {
			return true;
		}
		*///?}

		//? if repurposed_structures {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("repurposed_structures") && structure instanceof GenericJigsawStructure) {
			return true;
		}
		*///?}

		if (structureJson != null && (structureJson.has("max_distance_from_center") || structureJson.has("max_depth") || structureJson.has("size") || structureJson.has("start_height") || structureJson.has("project_start_to_heightmap"))) {
			return true;
		}

		return false;
	}

	@Nullable
	//? if >= 1.21.9 {
	public static JigsawStructure.MaxDistance getMaxDistanceFromCenterForStructure(Structure structure, JsonObject structureJson)
	//?} else {
	/*public static Integer getMaxDistanceFromCenterForStructure(Structure structure, JsonObject structureJson)
	*///?}
	{
		if (structure instanceof JigsawStructure) {
			return ((JigsawStructureAccessor) structure).structurify$getOriginalMaxDistanceFromCenter();
		}

		// TODO lithostitched
		/*
		if (PlatformHooks.PLATFORM_HELPER.isModLoaded("litostitched") && structure instanceof AlternateJigsawStructure) {
			return new JigsawStructure.MaxDistance(((AlternateJigsawStructure) structure).config().maxDistanceFromCenter().horizontal(), ((AlternateJigsawStructure) structure).config().maxDistanceFromCenter().vertical());
		}*/

		//? if yungs_api {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("yungsapi") && structure instanceof YungJigsawStructure) {
			return ((YungJigsawStructure) structure).maxDistanceFromCenter;
		}
		*///?}

		//? if repurposed_structures {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("repurposed_structures") && structure instanceof GenericJigsawStructure) {
			//? if >= 1.21.10 {
			var maxDistanceFromCenter = ((GenericJigsawStructure) structure).maxDistanceFromCenter.orElse(null);

			if(maxDistanceFromCenter == null) {
				return null;
			} else {
				return new JigsawStructure.MaxDistance(maxDistanceFromCenter);
			}
			 //?} else {
			/^return ((GenericJigsawStructure) structure).maxDistanceFromCenter.orElse(null);
			^///?}
		}
		*///?}

		var serializationContext = StructurifyRegistryManagerProvider.getSerializationContext();

		if (serializationContext == null || structureJson == null || !structureJson.has("max_distance_from_center")) {
			return null;
		}

		if (!structureJson.has("max_distance_from_center")) {
			return null;
		}

		JsonElement maxDistanceJson = structureJson.get("max_distance_from_center");

		//? if >= 1.21.9 {
		return JigsawStructure.MaxDistance.CODEC
			.parse(serializationContext, maxDistanceJson)
			.result()
			.orElse(null);
		//?} else {
		/*if (!maxDistanceJson.isJsonPrimitive() || !maxDistanceJson.getAsJsonPrimitive().isNumber()) {
			return null;
		}

		return maxDistanceJson.getAsInt();
		*///?}
	}

	@Nullable
	public static Integer getSizeForStructure(Structure structure, JsonObject structureJson) {
		if (structure instanceof JigsawStructure) {
			return ((JigsawStructureAccessor) structure).structurify$getOriginalMaxDepth();
		}

		//? if yungs_api {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("yungsapi") && structure instanceof YungJigsawStructure) {
			return ((YungJigsawStructure) structure).maxDepth;
		}
		*///?}

		//? if repurposed_structures {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("repurposed_structures") && structure instanceof GenericJigsawStructure) {
			return ((GenericJigsawStructure) structure).size;
		}
		*///?}

		var serializationContext = StructurifyRegistryManagerProvider.getSerializationContext();

		if (serializationContext == null || structureJson == null || (!structureJson.has("size") && !structureJson.has("max_depth"))) {
			return null;
		}

		JsonElement sizeJson = structureJson.get("size");

		if (sizeJson != null && sizeJson.isJsonPrimitive() && sizeJson.getAsJsonPrimitive().isNumber()) {
			return sizeJson.getAsInt();
		}

		JsonElement maxDepthJson = structureJson.get("max_depth");

		if (maxDepthJson != null && maxDepthJson.isJsonPrimitive() && maxDepthJson.getAsJsonPrimitive().isNumber()) {
			return maxDepthJson.getAsInt();
		}

		return null;
	}

	@Nullable
	public static HeightProvider getHeightProviderForStructure(Structure structure, JsonObject structureJson) {
		if (structure instanceof JigsawStructure) {
			return ((JigsawStructureAccessor) structure).structurify$getOriginalStartHeight();
		}

		//? if yungs_api {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("yungsapi") && structure instanceof YungJigsawStructure) {
			return ((YungJigsawStructure) structure).startHeight;
		}
		*///?}

		//? if repurposed_structures {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("repurposed_structures") && structure instanceof GenericJigsawStructure) {
			return ((GenericJigsawStructure) structure).startHeight;
		}
		*///?}

		var serializationContext = StructurifyRegistryManagerProvider.getSerializationContext();

		if (serializationContext == null || structureJson == null || !structureJson.has("start_height")) {
			return null;
		}

		JsonElement startHeightJson = structureJson.get("start_height");

		if (!startHeightJson.isJsonObject()) {
			return null;
		}

		return HeightProvider.CODEC
			.parse(serializationContext, startHeightJson)
			.result()
			.orElse(null);
	}

	public static @Nullable Optional<Heightmap.Types> getProjectStartToHeightMap(Structure structure, JsonObject structureJson) {
		if (structure instanceof JigsawStructure) {
			return ((JigsawStructureAccessor) structure).structurify$getOriginalProjectStartToHeightmap();
		}

		//? if yungs_api {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("yungsapi") && structure instanceof YungJigsawStructure) {
			return ((YungJigsawStructure) structure).projectStartToHeightmap;
		}
		*///?}

		//? if repurposed_structures {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("repurposed_structures") && structure instanceof GenericJigsawStructure) {
			return ((GenericJigsawStructure) structure).projectStartToHeightmap;
		}
		*///?}

		var serializationContext = StructurifyRegistryManagerProvider.getSerializationContext();

		if (serializationContext == null || structureJson == null || !structureJson.has("project_start_to_heightmap")) {
			return null;
		}

		JsonElement startHeightJson = structureJson.get("project_start_to_heightmap");

		return Heightmap.Types.CODEC
			.parse(serializationContext, startHeightJson)
			.result();
	}
}
