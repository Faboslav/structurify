package com.faboslav.structurify.common.config;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.google.gson.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class StructurifyConfig
{
	public boolean isLoaded = false;
	public boolean isLoading = false;
	private final Path configPath = Path.of("config", Structurify.MOD_ID + ".json");
	private final Path backupConfigPath = Path.of("config", Structurify.MOD_ID + "_backup.json");
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

	public void create() {
		if (Files.exists(configPath)) {
			return;
		}

		this.save();
	}

	public void load() {
		if (this.isLoading) {
			return;
		}

		try {
			Structurify.getLogger().info("Loading Structurify config...");
			this.isLoading = true;

			WorldgenDataProvider.loadWorldgenData();
			this.structureData = WorldgenDataProvider.getStructures();
			this.structureSetData = WorldgenDataProvider.getStructureSets();

			if (!Files.exists(configPath)) {
				return;
			}

			String jsonString = Files.readString(configPath);
			JsonObject json = gson.fromJson(jsonString, JsonObject.class);

			if (json.has("general")) {
				var general = json.getAsJsonObject("general");

				if (general.has("disable_all_structures")) {
					this.disableAllStructures = general.get("disabled_all_structures").getAsBoolean();
				}

				if (general.has("enable_global_spacing_and_separation_modifier")) {
					this.enableGlobalSpacingAndSeparationModifier = general.get("enable_global_spacing_and_separation_modifier").getAsBoolean();
				}

				if (general.has("global_spacing_and_separation_modifier")) {
					this.globalSpacingAndSeparationModifier = Math.round(general.get("global_spacing_and_separation_modifier").getAsDouble() * 10.0) / 10.0;
				}
			}

			if (json.has("structures")) {
				var structures = json.getAsJsonArray("structures");

				for (JsonElement structure : structures) {
					var structureJson = structure.getAsJsonObject();

					if (
						!structureJson.has("name")
						|| !structureJson.has("is_disabled")
						|| !structureJson.has("enable_biome_check")
						|| !structureJson.has("biome_check_distance")
						|| (
							!structureJson.has("biomes")
							&& (
								!structureJson.has("whitelisted_biomes")
								|| !structureJson.has("blacklisted_biomes")
							)
						)
					) {
						Structurify.getLogger().info("Found invalid structure entry, skipping.");
						continue;
					}

					if (!this.structureData.containsKey(structureJson.get("name").getAsString())) {
						Structurify.getLogger().info("Found invalid structure identifier of \"{}\", skipping.", structureJson.get("name").getAsString());
						continue;
					}

					var structureData = this.structureData.get(structureJson.get("name").getAsString());
					structureData.setDisabled(structureJson.get("is_disabled").getAsBoolean());

					var isBiomeCheckEnabled = structureJson.get("enable_biome_check").getAsBoolean();
					structureData.setEnableBiomeCheck(isBiomeCheckEnabled);

					var biomeCheckDistance = structureJson.get("biome_check_distance").getAsInt();
					structureData.setBiomeCheckDistance(biomeCheckDistance);

					List<String> biomes = new ArrayList<>(structureData.getDefaultBiomes());

					if (structureJson.has("biomes")) {
						var whitelistedBiomes = structureJson.getAsJsonArray("biomes").asList().stream().map(JsonElement::getAsString).collect(Collectors.toCollection(ArrayList::new));
						whitelistedBiomes.removeAll(structureData.getDefaultBiomes());
						whitelistedBiomes.stream().distinct().forEach(biomes::add);
						Structurify.getLogger().info("will add: " + whitelistedBiomes.toString());

						var blacklistedBiomes = new ArrayList<>(structureData.getDefaultBiomes());
						blacklistedBiomes.removeAll(structureJson.getAsJsonArray("biomes").asList().stream().map(JsonElement::getAsString).collect(Collectors.toCollection(ArrayList::new)));
						blacklistedBiomes.stream().distinct().forEach(biomes::remove);
						Structurify.getLogger().info("will remove: " + blacklistedBiomes.toString());
					} else {
						var whitelistedBiomes = structureJson.getAsJsonArray("whitelisted_biomes");
						for (JsonElement whitelistedBiome : whitelistedBiomes) {
							if (biomes.contains(whitelistedBiome.getAsString())) {
								continue;
							}

							biomes.add(whitelistedBiome.getAsString());
						}

						var blacklistedBiomes = structureJson.getAsJsonArray("blacklisted_biomes");

						for (JsonElement blacklistedBiome : blacklistedBiomes) {
							if (!biomes.contains(blacklistedBiome.getAsString())) {
								continue;
							}

							biomes.remove(blacklistedBiome.getAsString());
						}
					}

					structureData.setBiomes(biomes);
				}
			}

			if (json.has("structure_sets")) {
				var structureSets = json.getAsJsonArray("structure_sets");

				for (JsonElement structureSet : structureSets) {
					var structureSpreadJson = structureSet.getAsJsonObject();

					if (
						!structureSpreadJson.has("name")
						|| !structureSpreadJson.has("spacing")
						|| !structureSpreadJson.has("separation")
					) {
						Structurify.getLogger().info("Found invalid structure set entry, skipping.");
						continue;
					}

					if (!this.structureSetData.containsKey(structureSpreadJson.get("name").getAsString())) {
						Structurify.getLogger().info("Found invalid structure set identifier of \"{}\", skipping.", structureSpreadJson.get("name").getAsString());
						continue;
					}

					var structureSetName = structureSpreadJson.get("name").getAsString();
					var structureSetData = this.structureSetData.get(structureSpreadJson.get("name").getAsString());

					var spacing = structureSpreadJson.get("spacing").getAsInt();
					var separation = structureSpreadJson.get("separation").getAsInt();

					if (separation >= spacing) {
						Structurify.getLogger().info("Separatiton value for structure set {} is currently {}, which is bigger than spacing {}, value will be automatically corrected to {}.", structureSetName, separation, spacing, spacing - 1);
						separation = spacing - 1;
					}

					if (separation < 0) {
						Structurify.getLogger().info("Separatiton value for structure set {} is currently {}, which is lower than minimum value of zero, value will be automatically corrected to 0.", structureSetName, separation);
						separation = 0;
					}

					if (spacing < 1) {
						Structurify.getLogger().info("Spacing value for structure set {} is currently {}, which is lower than minimum value of zero, value will be automatically corrected to 0.", structureSetName, spacing);
						separation = 0;
					}

					structureSetData.setSpacing(spacing);
					structureSetData.setSeparation(separation);
				}
			}

			Structurify.getLogger().info("Structurify config loaded");
			this.isLoaded = true;
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to load Structurify config");
			e.printStackTrace();
		} finally {
			this.isLoading = false;
		}
	}

	public void save() {
		Structurify.getLogger().info("Saving Structurify config...");

		try {
			if (Files.exists(configPath)) {
				Files.deleteIfExists(backupConfigPath);
				Files.move(configPath, backupConfigPath);
			}

			JsonObject json = new JsonObject();

			this.saveGeneralData(json);
			this.saveStructuresData(json);
			this.saveStructureSetsData(json);

			Files.createFile(configPath);
			Files.writeString(configPath, gson.toJson(json));
			Files.deleteIfExists(backupConfigPath);

			Structurify.getLogger().info("Structurify config saved");
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to save Structurify config");
			e.printStackTrace();

			try {
				Structurify.getLogger().error("Restoring Structurify backup config...");
				Files.delete(backupConfigPath);
				Files.move(backupConfigPath, configPath);
			} catch (Exception fe) {
				Structurify.getLogger().error("Failed to restore Structurify backup config");
				fe.printStackTrace();
			}
		}
	}

	private void saveGeneralData(JsonObject json) {
		JsonObject general = new JsonObject();

		general.addProperty("disabled_all_structures", this.disableAllStructures);
		general.addProperty("enable_global_spacing_and_separation_modifier", this.enableGlobalSpacingAndSeparationModifier);
		general.addProperty("global_spacing_and_separation_modifier", Math.round(this.globalSpacingAndSeparationModifier * 10.0) / 10.0);

		json.add("general", general);
	}

	private void saveStructuresData(JsonObject json) {
		JsonArray structures = new JsonArray();

		this.structureData.entrySet().stream()
			.filter(structureDataEntry -> {
				if (structureDataEntry.getValue().isDisabled()) {
					return true;
				} else if (structureDataEntry.getValue().isBiomeCheckEnabled()) {
					return true;
				}

				var biomes = new ArrayList<>(structureDataEntry.getValue().getBiomes());
				var defaultBiomes = new ArrayList<>(structureDataEntry.getValue().getDefaultBiomes());

				Collections.sort(biomes);
				Collections.sort(defaultBiomes);

				return !biomes.equals(defaultBiomes);
			})
			.forEach(structureDataEntry -> {
				JsonObject structure = new JsonObject();
				structure.addProperty("name", structureDataEntry.getKey());
				structure.addProperty("is_disabled", structureDataEntry.getValue().isDisabled());

				structure.addProperty("enable_biome_check", structureDataEntry.getValue().isBiomeCheckEnabled());
				structure.addProperty("biome_check_distance", structureDataEntry.getValue().getBiomeCheckDistance());

				var whitelistedBiomes = new ArrayList<>(structureDataEntry.getValue().getBiomes());
				whitelistedBiomes.removeAll(structureDataEntry.getValue().getDefaultBiomes());
				JsonArray whitelistedBiomesJson = new JsonArray();
				whitelistedBiomes.stream().distinct().forEach(whitelistedBiomesJson::add);
				structure.add("whitelisted_biomes", whitelistedBiomesJson);

				var blacklistedBiomes = new ArrayList<>(structureDataEntry.getValue().getDefaultBiomes());
				blacklistedBiomes.removeAll(structureDataEntry.getValue().getBiomes());
				JsonArray blacklistedBiomesJson = new JsonArray();
				blacklistedBiomes.stream().distinct().forEach(blacklistedBiomesJson::add);
				structure.add("blacklisted_biomes", blacklistedBiomesJson);

				structures.add(structure);
			});

		json.add("structures", structures);
	}

	private void saveStructureSetsData(JsonObject json) {
		JsonArray structureSets = new JsonArray();

		this.structureSetData.entrySet().stream()
			.filter(entry -> !entry.getValue().isUsingDefaultSpacing() || !entry.getValue().isUsingDefaultSeparation())
			.forEach(entry -> {
				var structureSetName = entry.getKey();
				var spacing = entry.getValue().getSpacing();
				var separation = entry.getValue().getSeparation();

				if (separation >= spacing) {
					Structurify.getLogger().info("Separatiton value for structure set {} is currently {}, which is bigger than spacing {}, value will be automatically corrected to {}. ", structureSetName, separation, spacing, spacing - 1);
					separation = spacing - 1;
				}

				if (separation < 0) {
					Structurify.getLogger().info("Separatiton value for structure set {} is currently {}, which is lower than minimum value of zero, value will be automatically corrected to 0.", structureSetName, separation);
					separation = 0;
				}

				if (spacing < 1) {
					Structurify.getLogger().info("Spacing value for structure set {} is currently {}, which is lower than minimum value of zero, value will be automatically corrected to 0.", structureSetName, spacing);
					separation = 0;
				}

				JsonObject specificStructureSpread = new JsonObject();
				specificStructureSpread.addProperty("name", entry.getKey());
				specificStructureSpread.addProperty("spacing", spacing);
				specificStructureSpread.addProperty("separation", separation);
				structureSets.add(specificStructureSpread);
			});

		json.add("structure_sets", structureSets);
	}
}