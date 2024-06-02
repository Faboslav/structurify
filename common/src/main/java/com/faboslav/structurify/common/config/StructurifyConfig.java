package com.faboslav.structurify.common.config;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.google.gson.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class StructurifyConfig
{
	public boolean isLoaded = false;
	private final Path configPath = Path.of("config", Structurify.MOD_ID + ".json");
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public boolean enableGlobalSpacingAndSeparationModifier = true;
	public double globalSpacingAndSeparationModifier = 1.0D;

	public boolean disableAllStructures = false;
	private Map<String, StructureData> structureData = new TreeMap<>();
	private Map<String, StructureSetData> structureSetData = new TreeMap<>();

	public Map<String, StructureData> getStructureData() {
		return this.structureData;
	}

	public Map<String, StructureSetData> getStructureSetData() {
		return this.structureSetData;
	}

	public void load() {
		Structurify.getLogger().info("Loading Structurify config...");

		try {
			WorldgenDataProvider.reload();
			this.structureData = WorldgenDataProvider.getStructures();
			this.structureSetData = WorldgenDataProvider.getStructureSets();

			if (!Files.exists(configPath)) {
				return;
			}

			String jsonString = Files.readString(configPath);
			JsonObject json = gson.fromJson(jsonString, JsonObject.class);

			if (json.has("disabconfig.disabled_all_structures")) {
				this.disableAllStructures = json.get("disabled_all_structures").getAsBoolean();
			}

			if (json.has("enable_global_spacing_and_separation_modifier")) {
				this.enableGlobalSpacingAndSeparationModifier = json.get("enable_global_spacing_and_separation_modifier").getAsBoolean();
			}

			if (json.has("global_spacing_and_separation_modifier")) {
				this.globalSpacingAndSeparationModifier = json.get("global_spacing_and_separation_modifier").getAsDouble();
			}

			if (json.has("disabled_structures")) {
				var disabledStructuresIds = json.getAsJsonArray("disabled_structures");

				for (JsonElement disabledStructureId : disabledStructuresIds) {
					if (!this.structureData.containsKey(disabledStructureId.getAsString())) {
						continue;
					}
					this.structureData.get(disabledStructureId.getAsString()).setDisabled(true);
				}
			}

			if (json.has("structures")) {
				var structures = json.getAsJsonArray("structures");

				for (JsonElement structure : structures) {
					var structureJson = structure.getAsJsonObject();

					if (!structureJson.has("name") || !structureJson.has("is_disabled") || !structureJson.has("blacklisted_biomes")) {
						// TODO warning
						continue;
					}

					if (!this.structureData.containsKey(structureJson.get("name").getAsString())) {
						// TODO warning
						continue;
					}

					var structureData = this.structureData.get(structureJson.get("name").getAsString());
					structureData.setDisabled(structureJson.get("is_disabled").getAsBoolean());

					var blacklistedBiomesJson = structureJson.getAsJsonArray("blacklisted_biomes");
					List<String> blacklistedBiomes = new ArrayList<>();

					for (JsonElement blacklistedBiome : blacklistedBiomesJson) {
						blacklistedBiomes.add(blacklistedBiome.getAsString());
					}

					structureData.setBlacklistedBiomes(blacklistedBiomes);
				}
			}

			if (json.has("structure_sets")) {
				var structureSets = json.getAsJsonArray("structure_sets");

				for (JsonElement structureSet : structureSets) {
					var structureSpreadJson = structureSet.getAsJsonObject();

					if (!structureSpreadJson.has("name") || !structureSpreadJson.has("spacing") || !structureSpreadJson.has("separation")) {
						// TODO warning
						continue;
					}

					if (!this.structureSetData.containsKey(structureSpreadJson.get("name").getAsString())) {
						continue;
					}

					var structureSetData = this.structureSetData.get(structureSpreadJson.get("name").getAsString());
					structureSetData.setSpacing(structureSpreadJson.get("spacing").getAsInt());
					structureSetData.setSeparation(structureSpreadJson.get("separation").getAsInt());
				}
			}

			Structurify.getLogger().info("Structurify config loaded");
			this.isLoaded = true;
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to load Structurify config");
			e.printStackTrace();
		}
	}

	public void save() {
		Structurify.getLogger().info("Saving Structurify config...");

		try {
			Files.deleteIfExists(configPath);
			JsonObject json = new JsonObject();

			json.addProperty("disabled_all_structures", this.disableAllStructures);
			json.addProperty("enable_global_spacing_and_separation_modifier", this.enableGlobalSpacingAndSeparationModifier);
			json.addProperty("global_spacing_and_separation_modifier", this.globalSpacingAndSeparationModifier);

			JsonArray structures = new JsonArray();

			this.structureData.entrySet().stream()
				.filter(entry -> entry.getValue().isDisabled() || !entry.getValue().getBlacklistedBiomes().isEmpty())
				.forEach(entry -> {
					JsonObject structure = new JsonObject();
					structure.addProperty("name", entry.getKey());
					structure.addProperty("is_disabled", entry.getValue().isDisabled());

					JsonArray blacklistedBiomes = new JsonArray();
					entry.getValue().getBlacklistedBiomes().forEach(blacklistedBiomes::add);
					structure.add("blacklisted_biomes", blacklistedBiomes);
					structures.add(structure);
				});

			json.add("structures", structures);

			JsonArray structureSets = new JsonArray();
			this.structureSetData.entrySet().stream()
				.filter(entry -> !entry.getValue().isUsingDefaultSpacing() || !entry.getValue().isUsingDefaultSeparation())
				.forEach(entry -> {
					var spacing = entry.getValue().getSpacing();
					var separation = entry.getValue().getSeparation();

					if(separation >= spacing) {
						Structurify.getLogger().info("Separatiton value for structure set {} is currently {}, which is bigger than spacing {}, value will be automatically corrected to {}. ", entry.getKey(), separation, spacing, spacing - 1);
						separation = spacing - 1;
					}

					JsonObject specificStructureSpread = new JsonObject();
					specificStructureSpread.addProperty("name", entry.getKey());
					specificStructureSpread.addProperty("spacing", spacing);
					specificStructureSpread.addProperty("separation", separation);
					structureSets.add(specificStructureSpread);
				});

			json.add("structure_sets", structureSets);

			Files.createFile(configPath);
			Files.writeString(configPath, gson.toJson(json));
			Structurify.getLogger().info("Structurify config saved");
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to save Structurify config");
			e.printStackTrace();
		}
	}
}