package com.faboslav.structurify.common.config;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.google.gson.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class StructurifyConfig
{
	public boolean isLoaded = false;
	private final Path configPath = Path.of("config", Structurify.MOD_ID + ".json");
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public boolean enableGlobalSpacingAndSeparationModifier = true;
	public double globalSpacingAndSeparationModifier = 1.0D;

	public boolean disableAllStructures = false;
	private final Map<String, Boolean> jsonStructures = new HashMap<>();
	private Map<String, StructureData> structureData = new TreeMap<>();
	private Map<String, StructureSetData> structureSetData = new TreeMap<>();

	public Map<String, StructureData> getStructureData() {
		return this.structureData;
	}

	public Map<String, StructureSetData> getStructureSetData() {
		return this.structureSetData;
	}

	public void load() {
		Structurify.getLogger().info("Loading Structurify");

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

			Structurify.getLogger().info("sETTTING IS LOADED");
			this.isLoaded = true;
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to load structurify config");
			e.printStackTrace();
		}
	}

	public void save() {
		Structurify.getLogger().info("Saving Structurify Config");

		try {
			Files.deleteIfExists(configPath);
			JsonObject json = new JsonObject();

			json.addProperty("disabled_all_structures", this.disableAllStructures);
			json.addProperty("enable_global_spacing_and_separation_modifier", this.enableGlobalSpacingAndSeparationModifier);
			json.addProperty("global_spacing_and_separation_modifier", this.globalSpacingAndSeparationModifier);

			JsonArray disabledStructures = new JsonArray();
			this.structureData.entrySet().stream()
				.filter(entry -> entry.getValue().isDisabled())
				.forEach(entry -> disabledStructures.add(entry.getKey()));
			json.add("disabled_structures", disabledStructures);

			JsonArray structureSets = new JsonArray();
			this.structureSetData.entrySet().stream()
				.filter(entry -> !entry.getValue().isUsingDefaultSpacing() || !entry.getValue().isUsingDefaultSeparation())
				.forEach(entry -> {
					Structurify.getLogger().info("Adding structure set " + entry.getKey());
					Structurify.getLogger().info("Spacing: " + String.valueOf(entry.getValue().getSpacing()));
					Structurify.getLogger().info("Separation: " + String.valueOf(entry.getValue().getSeparation()));
					JsonObject specificStructureSpread = new JsonObject();
					specificStructureSpread.addProperty("name", entry.getKey());
					specificStructureSpread.addProperty("spacing", entry.getValue().getSpacing());
					specificStructureSpread.addProperty("separation", entry.getValue().getSeparation());
					structureSets.add(specificStructureSpread);
				});

			json.add("structure_sets", structureSets);

			Files.createFile(configPath);
			Files.writeString(configPath, gson.toJson(json));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}