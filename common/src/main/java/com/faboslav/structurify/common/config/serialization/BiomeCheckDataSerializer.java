package com.faboslav.structurify.common.config.serialization;

import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.stream.StreamSupport;

public final class BiomeCheckDataSerializer
{
	private static final String OVERRIDE_GLOBAL_BIOME_CHECK_PROPERTY = "override_global_biome_check";
	private static final String ENABLE_BIOME_CHECK_PROPERTY = "enable_biome_check";
	private static final String BIOME_CHECK_MODE_PROPERTY = "biome_check_mode";
	private static final String BIOME_CHECK_BLACKLISTED_BIOMES_PROPERTY = "biome_check_blacklisted_biomes";

	public static void load(JsonObject structureJson, BiomeCheckData biomeCheckData) {
		if (structureJson.has(OVERRIDE_GLOBAL_BIOME_CHECK_PROPERTY)) {
			var overrideGlobalBiomeCheck = structureJson.get(OVERRIDE_GLOBAL_BIOME_CHECK_PROPERTY).getAsBoolean();
			biomeCheckData.overrideGlobalBiomeCheck(overrideGlobalBiomeCheck);
		}

		if (structureJson.has(ENABLE_BIOME_CHECK_PROPERTY)) {
			var isBiomeCheckEnabled = structureJson.get(ENABLE_BIOME_CHECK_PROPERTY).getAsBoolean();
			biomeCheckData.enable(isBiomeCheckEnabled);
		}

		if (structureJson.has(BIOME_CHECK_MODE_PROPERTY)) {
			var biomeCheckMode = structureJson.get(BIOME_CHECK_MODE_PROPERTY).getAsString();
			biomeCheckData.setMode(BiomeCheckData.BiomeCheckMode.valueOf(biomeCheckMode));
		}

		if (structureJson.has(BIOME_CHECK_BLACKLISTED_BIOMES_PROPERTY)) {
			var biomeCheckBlacklistedBiomes = StreamSupport
				.stream(structureJson.getAsJsonArray(BIOME_CHECK_BLACKLISTED_BIOMES_PROPERTY).spliterator(), false)
				.map(JsonElement::getAsString)
				.toList();
			biomeCheckData.setBlacklistedBiomes(biomeCheckBlacklistedBiomes);
		}
	}

	public static void save(JsonObject structureJson, BiomeCheckData biomeCheckData) {
		structureJson.addProperty(OVERRIDE_GLOBAL_BIOME_CHECK_PROPERTY, biomeCheckData.isOverridingGlobalBiomeCheck());
		structureJson.addProperty(ENABLE_BIOME_CHECK_PROPERTY, biomeCheckData.isEnabled());
		structureJson.addProperty(BIOME_CHECK_MODE_PROPERTY, biomeCheckData.getMode().name());

		var biomeCheckBlacklistedBiomes = new ArrayList<>(biomeCheckData.getBlacklistedBiomes());
		JsonArray biomeCheckBlacklistedBiomesJson = new JsonArray();
		biomeCheckBlacklistedBiomes.stream().distinct().forEach(biomeCheckBlacklistedBiomesJson::add);
		structureJson.add(BIOME_CHECK_BLACKLISTED_BIOMES_PROPERTY, biomeCheckBlacklistedBiomesJson);
	}
}
