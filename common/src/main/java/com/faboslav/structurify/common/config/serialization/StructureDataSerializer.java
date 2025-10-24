package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.faboslav.structurify.common.config.data.structure.JigsawData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class StructureDataSerializer
{
	public static final String NAME_PROPERTY = "name";
	private static final String IS_DISABLED_PROPERTY = "is_disabled";
	private static final String BIOMES_PROPERTY = "biomes";
	private static final String WHITELISTED_BIOMES_PROPERTY = "whitelisted_biomes";
	private static final String BLACKLISTED_BIOMES_PROPERTY = "blacklisted_biomes";
	public static final String STEP_PROPERTY = "step";
	public static final String TERRAIN_ADAPTATION_PROPERTY = "terrain_adaptation";

	public static void load(JsonObject structureJson, StructureData structureData) {
		var structureName = structureJson.get(NAME_PROPERTY).getAsString();

		if (structureJson.has(IS_DISABLED_PROPERTY)) {
			structureData.setDisabled(structureJson.get(IS_DISABLED_PROPERTY).getAsBoolean());
		}

		List<String> biomes = new ArrayList<>(structureData.getDefaultBiomes());

		if (structureJson.has(BIOMES_PROPERTY)) {
			var whitelistedBiomes = structureJson.getAsJsonArray(BIOMES_PROPERTY).asList().stream().map(JsonElement::getAsString).collect(Collectors.toCollection(ArrayList::new));
			whitelistedBiomes.removeAll(structureData.getDefaultBiomes());
			whitelistedBiomes.stream().distinct().forEach(biomes::add);

			var blacklistedBiomes = new ArrayList<>(structureData.getDefaultBiomes());
			blacklistedBiomes.removeAll(structureJson.getAsJsonArray(BIOMES_PROPERTY).asList().stream().map(JsonElement::getAsString).collect(Collectors.toCollection(ArrayList::new)));
			blacklistedBiomes.stream().distinct().forEach(biomes::remove);
		} else if(structureJson.has(WHITELISTED_BIOMES_PROPERTY) && structureJson.has(BLACKLISTED_BIOMES_PROPERTY)){
			var whitelistedBiomes = structureJson.getAsJsonArray(WHITELISTED_BIOMES_PROPERTY);
			for (JsonElement whitelistedBiome : whitelistedBiomes) {
				if (biomes.contains(whitelistedBiome.getAsString())) {
					continue;
				}

				biomes.add(whitelistedBiome.getAsString());
			}

			var blacklistedBiomes = structureJson.getAsJsonArray(BLACKLISTED_BIOMES_PROPERTY);

			for (JsonElement blacklistedBiome : blacklistedBiomes) {
				if (!biomes.contains(blacklistedBiome.getAsString())) {
					continue;
				}

				biomes.remove(blacklistedBiome.getAsString());
			}
		}

		structureData.setBiomes(biomes);

		if (structureJson.has(STEP_PROPERTY)) {
			String rawStep = structureJson.get(STEP_PROPERTY).getAsString();
			GenerationStep.Decoration step = Arrays.stream(GenerationStep.Decoration.values())
				.filter(s -> s.getSerializedName().equals(rawStep))
				.findFirst()
				.orElseGet(() -> {
					Structurify.getLogger().info("Step value for structure {} is currently '{}', which is invalid. Value will be corrected to {}.", structureName, rawStep, structureData.getDefaultStep().getSerializedName());
					return structureData.getDefaultStep();
				});

			structureData.setStep(step);
		}

		if (structureJson.has(TERRAIN_ADAPTATION_PROPERTY)) {
			String rawTerrainAdaptation = structureJson.get(TERRAIN_ADAPTATION_PROPERTY).getAsString();
			TerrainAdjustment terrainAdaptation = Arrays.stream(TerrainAdjustment.values())
				.filter(s -> s.getSerializedName().equals(rawTerrainAdaptation))
				.findFirst()
				.orElseGet(() -> {
					Structurify.getLogger().info("Terrain adaptation value for structure {} is currently '{}', which is invalid. Value will be corrected to {}.", structureName, rawTerrainAdaptation, structureData.getDefaultTerrainAdaptation().getSerializedName());
					return structureData.getDefaultTerrainAdaptation();
				});

			structureData.setTerrainAdaptation(terrainAdaptation);
		}

		if(structureData.isJigsawStructure()) {
			JigsawDataSerializer.load(structureJson, structureData.getJigsawData());
		}

		FlatnessCheckDataSerializer.load(structureJson, structureData.getFlatnessCheckData());
		BiomeCheckDataSerializer.load(structureJson, structureData.getBiomeCheckData());
	}

	public static void save(JsonArray structuresJson, String structureName, StructureData structureData) {
		JsonObject structure = new JsonObject();

		structure.addProperty(NAME_PROPERTY, structureName);
		structure.addProperty(IS_DISABLED_PROPERTY, structureData.isDisabled());

		var whitelistedBiomes = new ArrayList<>(structureData.getBiomes());
		whitelistedBiomes.removeAll(structureData.getDefaultBiomes());
		JsonArray whitelistedBiomesJson = new JsonArray();
		whitelistedBiomes.stream().distinct().forEach(whitelistedBiomesJson::add);
		structure.add(WHITELISTED_BIOMES_PROPERTY, whitelistedBiomesJson);

		var blacklistedBiomes = new ArrayList<>(structureData.getDefaultBiomes());
		blacklistedBiomes.removeAll(structureData.getBiomes());
		JsonArray blacklistedBiomesJson = new JsonArray();
		blacklistedBiomes.stream().distinct().forEach(blacklistedBiomesJson::add);
		structure.add(BLACKLISTED_BIOMES_PROPERTY, blacklistedBiomesJson);

		structure.addProperty(STEP_PROPERTY, structureData.getStep().getSerializedName());
		structure.addProperty(TERRAIN_ADAPTATION_PROPERTY, structureData.getTerrainAdaptation().getSerializedName());

		if(structureData.isJigsawStructure()) {
			var jigsawData = structureData.getJigsawData();

			if(!jigsawData.isUsingDefaultValues()) {
				JigsawDataSerializer.save(structure, jigsawData);
			}
		}

		var distanceFromWorldCenterData = structureData.getDistanceFromWorldCenterCheckData();
		if(!distanceFromWorldCenterData.isUsingDefaultValues()) {
			DistanceFromWorldCenterDataSerializer.save(structure, distanceFromWorldCenterData);
		}

		var flatnessCheckData = structureData.getFlatnessCheckData();
		if (!flatnessCheckData.isUsingDefaultValues()) {
			FlatnessCheckDataSerializer.save(structure, flatnessCheckData);
		}

		var biomeCheckData = structureData.getBiomeCheckData();
		if(!biomeCheckData.isUsingDefaultValues()) {
			BiomeCheckDataSerializer.save(structure, biomeCheckData);
		}

		structuresJson.add(structure);
	}
}
