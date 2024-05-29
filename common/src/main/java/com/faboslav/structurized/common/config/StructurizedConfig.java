package com.faboslav.structurized.common.config;

import com.faboslav.structurized.common.Structurized;
import com.faboslav.structurized.common.config.data.StructureData;
import com.faboslav.structurized.common.config.data.StructureSetData;
import com.faboslav.structurized.common.config.data.WorldgenDataProvider;
import com.google.gson.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class StructurizedConfig
{
	public boolean isLoaded = false;
	private final Path configPath = Path.of("config", "structurized.json");
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public boolean enableGlobalSpacingAndSeparation = true;
	public double globalSpacingAndSeparationModifier = 1.0D;
	public double globalSpacingModifier = 1.0D;
	public double globalSeparationModifier = 1.0D;
	public int spacingPlaceholder = 1;
	public int separationPlaceholder = 1;

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
		Structurized.getLogger().info("Loading Structurized");

		try {
			String jsonString = Files.readString(configPath);
			JsonObject json = gson.fromJson(jsonString, JsonObject.class);

			if (json.has("disabconfig.globalSpacingModifierled_all_structures")) {
				this.disableAllStructures = json.get("disabled_all_structures").getAsBoolean();
			}

			if (json.has("enable_global_spacing_and_separation")) {
				this.enableGlobalSpacingAndSeparation = json.get("enable_global_spacing_and_separation").getAsBoolean();
			}

			if (json.has("global_spacing_modifier")) {
				this.globalSpacingModifier = json.get("global_spacing_modifier").getAsDouble();
			}

			if (json.has("global_separation_modifier")) {
				this.globalSpacingModifier = json.get("global_separation_modifier").getAsDouble();
			}

			this.structureData = WorldgenDataProvider.getStructures();

			if (json.has("disabled_structures")) {
				var disabledStructuresIds = json.getAsJsonArray("disabled_structures");

				for (JsonElement disabledStructureId : disabledStructuresIds) {
					if(!this.structureData.containsKey(disabledStructureId.getAsString())) {
						continue;
					}
					this.structureData.get(disabledStructureId.getAsString()).setDisabled(true);
				}
			}

			this.structureSetData = WorldgenDataProvider.getStructureSets();

			if (json.has("structure_spreads")) {
				var structureSpreads = json.getAsJsonArray("structure_spreads");

				for (JsonElement structureSpread : structureSpreads) {
					var structureSpreadJson = structureSpread.getAsJsonObject();

					if(!structureSpreadJson.has("name") || !structureSpreadJson.has("spacing") || !structureSpreadJson.has("separation")) {
						// TODO warning
						continue;
					}

					if(!this.structureSetData.containsKey(structureSpreadJson.get("name").getAsString())) {
						continue;
					}

					var structureSet = this.structureSetData.get(structureSpreadJson.get("name").getAsString());
					structureSet.setSpacing(structureSpreadJson.get("spacing").getAsInt());
					structureSet.setSeparation(structureSpreadJson.get("separation").getAsInt());
				}
			}

			this.structureSetData = WorldgenDataProvider.getStructureSets();
			this.isLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		Structurized.getLogger().info("Saving Structurized Config");

		try {
			Files.deleteIfExists(configPath);
			JsonObject json = new JsonObject();

			JsonArray disabledStructures = new JsonArray();
			this.structureData.entrySet().stream()
				.filter(entry -> entry.getValue().isDisabled())
				.forEach(entry -> disabledStructures.add(entry.getKey()));

			json.addProperty("disabled_all_structures", this.disableAllStructures);
			json.addProperty("enable_global_spacing_and_separation_modifier", this.enableGlobalSpacingAndSeparation);
			json.addProperty("global_spacing_modifier", this.globalSpacingModifier);
			json.addProperty("global_separation_modifier", this.globalSeparationModifier);
			json.add("disabled_structures", disabledStructures);

			JsonArray structureSpreads = new JsonArray();
			this.structureSetData.entrySet().stream()
				.filter(entry -> entry.getValue().getDefaultSpacing() != entry.getValue().getSpacing() || entry.getValue().getDefaultSeparation() != entry.getValue().getSeparation())
				.forEach(entry -> {
					JsonObject specificStructureSpread = new JsonObject();
					specificStructureSpread.addProperty("name", entry.getKey());
					specificStructureSpread.addProperty("spacing", entry.getValue().getSpacing());
					specificStructureSpread.addProperty("separation", entry.getValue().getSeparation());
					structureSpreads.add(specificStructureSpread);
				});

			json.add("structure_spreads", structureSpreads);

			Files.createFile(configPath);
			Files.writeString(configPath, gson.toJson(json));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}