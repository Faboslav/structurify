package com.faboslav.structurify.common.config;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.platform.PlatformHooks;
import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public final class StructurifyConfig
{
	private static final Path BACKUP_CONFIG_DIR = Path.of("config/structurify");
	private static final String BACKUP_PREFIX = Structurify.MOD_ID + "_backup_";
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

	public boolean isLoaded = false;
	public boolean isLoading = false;
	private final Path configPath = Path.of("config", Structurify.MOD_ID + ".json");
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public boolean disableAllStructures = false;
	public int minStructureDistanceFromWorldCenter = 0;
	public boolean enableGlobalSpacingAndSeparationModifier = ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE;
	public double globalSpacingAndSeparationModifier = GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE;

	private Map<String, StructureData> structureData = new TreeMap<>();
	private Map<String, StructureSetData> structureSetData = new TreeMap<>();

	public final static boolean ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE = false;
	public final static double GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE = 1.0D;

	private static final String CONFIG_VERSION_PROPERTY = "config_version";
	private static final String CONFIG_DATETIME_PROPERTY = "config_datetime";
	private static final String GENERAL_PROPERTY = "general";
	private static final String MIN_STRUCTURE_DISTANCE_FROM_WORLD_CENTER_PROPERTY = "min_structure_distance_from_world_center";
	private static final String DISABLE_ALL_STRUCTURES_PROPERTY = "disable_all_structures";
	private static final String ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY = "enable_global_spacing_and_separation_modifier";
	private static final String GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY = "global_spacing_and_separation_modifier";

	private static final String STRUCTURES_PROPERTY = "structures";
	private static final String NAME_PROPERTY = "name";
	private static final String IS_DISABLED_PROPERTY = "is_disabled";
	private static final String ENABLE_FLATNESS_CHECK_PROPERTY = "enable_flatness_check";
	private static final String FLATNESS_CHECK_DISTANCE_PROPERTY = "flatness_check_distance";
	private static final String FLATNESS_CHECK_THRESHOLD_PROPERTY = "flatness_check_threshold";
	private static final String ENABLE_BIOME_CHECK_PROPERTY = "enable_biome_check";
	private static final String BIOME_CHECK_DISTANCE_PROPERTY = "biome_check_distance";
	private static final String BIOMES_PROPERTY = "biomes";
	private static final String WHITELISTED_BIOMES_PROPERTY = "whitelisted_biomes";
	private static final String BLACKLISTED_BIOMES_PROPERTY = "blacklisted_biomes";

	private static final String STRUCTURE_SETS_PROPERTY = "structure_sets";
	private static final String SALT_PROPERTY = "salt";
	private static final String FREQUENCY_PROPERTY = "frequency";
	private static final String OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY = "override_global_spacing_and_separation_modifier";
	private static final String SPACING_PROPERTY = "spacing";
	private static final String SEPARATION_PROPERTY = "separation";

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

			if (json.has(GENERAL_PROPERTY)) {
				var general = json.getAsJsonObject(GENERAL_PROPERTY);

				if (general.has(DISABLE_ALL_STRUCTURES_PROPERTY)) {
					this.disableAllStructures = general.get(DISABLE_ALL_STRUCTURES_PROPERTY).getAsBoolean();
				}

				if (general.has(MIN_STRUCTURE_DISTANCE_FROM_WORLD_CENTER_PROPERTY)) {
					this.minStructureDistanceFromWorldCenter = general.get(MIN_STRUCTURE_DISTANCE_FROM_WORLD_CENTER_PROPERTY).getAsInt();
				}

				if (general.has(ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY)) {
					this.enableGlobalSpacingAndSeparationModifier = general.get(ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY).getAsBoolean();
				}

				if (general.has(GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY)) {
					this.globalSpacingAndSeparationModifier = Math.round(general.get(GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY).getAsDouble() * 10.0) / 10.0;
				}
			}

			if (json.has(STRUCTURES_PROPERTY)) {
				var structures = json.getAsJsonArray(STRUCTURES_PROPERTY);

				for (JsonElement structure : structures) {
					var structureJson = structure.getAsJsonObject();

					if (
						!structureJson.has(NAME_PROPERTY)
						|| !structureJson.has(IS_DISABLED_PROPERTY)
						|| !structureJson.has(ENABLE_BIOME_CHECK_PROPERTY)
						|| !structureJson.has(BIOME_CHECK_DISTANCE_PROPERTY)
						|| (
							!structureJson.has(BIOMES_PROPERTY)
							&& (
								!structureJson.has(WHITELISTED_BIOMES_PROPERTY)
								|| !structureJson.has(BLACKLISTED_BIOMES_PROPERTY)
							)
						)
					) {
						Structurify.getLogger().info("Found invalid structure entry, skipping.");
						continue;
					}

					if (!this.structureData.containsKey(structureJson.get(NAME_PROPERTY).getAsString())) {
						Structurify.getLogger().info("Found invalid structure identifier of \"{}\", skipping.", structureJson.get(NAME_PROPERTY).getAsString());
						continue;
					}

					var structureData = this.structureData.get(structureJson.get(NAME_PROPERTY).getAsString());
					structureData.setDisabled(structureJson.get(IS_DISABLED_PROPERTY).getAsBoolean());

					if (structureJson.has(ENABLE_FLATNESS_CHECK_PROPERTY)) {
						var isFlatnessCheckEnabled = structureJson.get(ENABLE_FLATNESS_CHECK_PROPERTY).getAsBoolean();
						structureData.setEnableFlatnessCheck(isFlatnessCheckEnabled);
					}

					if (structureJson.has(FLATNESS_CHECK_DISTANCE_PROPERTY)) {
						var flatnessCheckDistance = structureJson.get(FLATNESS_CHECK_DISTANCE_PROPERTY).getAsInt();
						structureData.setFlatnessCheckDistance(flatnessCheckDistance);
					}

					if (structureJson.has(FLATNESS_CHECK_THRESHOLD_PROPERTY)) {
						var flatnessCheckThreshold = structureJson.get(FLATNESS_CHECK_THRESHOLD_PROPERTY).getAsInt();
						structureData.setFlatnessCheckThreshold(flatnessCheckThreshold);
					}

					var isBiomeCheckEnabled = structureJson.get(ENABLE_BIOME_CHECK_PROPERTY).getAsBoolean();
					structureData.setEnableBiomeCheck(isBiomeCheckEnabled);

					var biomeCheckDistance = structureJson.get(BIOME_CHECK_DISTANCE_PROPERTY).getAsInt();
					structureData.setBiomeCheckDistance(biomeCheckDistance);

					List<String> biomes = new ArrayList<>(structureData.getDefaultBiomes());

					if (structureJson.has(BIOMES_PROPERTY)) {
						var whitelistedBiomes = structureJson.getAsJsonArray(BIOMES_PROPERTY).asList().stream().map(JsonElement::getAsString).collect(Collectors.toCollection(ArrayList::new));
						whitelistedBiomes.removeAll(structureData.getDefaultBiomes());
						whitelistedBiomes.stream().distinct().forEach(biomes::add);

						var blacklistedBiomes = new ArrayList<>(structureData.getDefaultBiomes());
						blacklistedBiomes.removeAll(structureJson.getAsJsonArray(BIOMES_PROPERTY).asList().stream().map(JsonElement::getAsString).collect(Collectors.toCollection(ArrayList::new)));
						blacklistedBiomes.stream().distinct().forEach(biomes::remove);
					} else {
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
				}
			}

			if (json.has(STRUCTURE_SETS_PROPERTY)) {
				var structureSets = json.getAsJsonArray(STRUCTURE_SETS_PROPERTY);

				for (JsonElement structureSet : structureSets) {
					var structureSpreadJson = structureSet.getAsJsonObject();

					if (!structureSpreadJson.has(NAME_PROPERTY)) {
						Structurify.getLogger().info("Found invalid structure set entry, skipping.");
						continue;
					}

					var structureSetName = structureSpreadJson.get(NAME_PROPERTY).getAsString();

					if (!this.structureSetData.containsKey(structureSetName)) {
						Structurify.getLogger().info("Found invalid structure set identifier of \"{}\", skipping.", structureSetName);
						continue;
					}

					var structureSetData = this.structureSetData.get(structureSetName);

					var salt = structureSpreadJson.has(SALT_PROPERTY) ? structureSpreadJson.get(SALT_PROPERTY).getAsInt() : structureSetData.getDefaultSalt();
					var frequency = structureSpreadJson.has(FREQUENCY_PROPERTY) ? structureSpreadJson.get(FREQUENCY_PROPERTY).getAsInt() : structureSetData.getDefaultFrequency();
					var spacing = structureSpreadJson.has(SPACING_PROPERTY) ? structureSpreadJson.get(SPACING_PROPERTY).getAsInt() : structureSetData.getDefaultSpacing();
					var separation = structureSpreadJson.has(SEPARATION_PROPERTY) ? structureSpreadJson.get(SEPARATION_PROPERTY).getAsInt() : structureSetData.getDefaultSeparation();

					if((salt < StructureSetData.MIN_SALT || salt > StructureSetData.MAX_SALT) && salt != structureSetData.getDefaultSalt()) {
						Structurify.getLogger().info("Salt value for structure set {} is currently {}, which is invalid, value will be automatically corrected to {}.", structureSetName, salt, structureSetData.getDefaultSalt());
						salt = structureSetData.getDefaultSalt();
					}

					if(frequency < StructureSetData.MIN_FREQUENCY || frequency > StructureSetData.MAX_FREQUENCY) {
						Structurify.getLogger().info("Frequency value for structure set {} is currently {}, which is invalid, value will be automatically corrected to {}.", structureSetName, frequency, structureSetData.getDefaultFrequency());
						frequency = structureSetData.getDefaultFrequency();
					}

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

					var overrideGlobalSpacingAndSeparationModifier = structureSpreadJson.get(OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY).getAsBoolean();

					structureSetData.setSalt(salt);
					structureSetData.setFrequency(frequency);
					structureSetData.setOverrideGlobalSpacingAndSeparationModifier(overrideGlobalSpacingAndSeparationModifier);
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
				// TODO delete all old backups here
				Path backupConfigPath = this.getBackupConfigPath();

				if(!Files.exists(BACKUP_CONFIG_DIR) || !Files.isDirectory(BACKUP_CONFIG_DIR)) {
					Files.createDirectories(BACKUP_CONFIG_DIR);
				}

				if(!Files.exists(backupConfigPath)) {
					Files.move(configPath, backupConfigPath);
				}
			}

			JsonObject json = new JsonObject();

			json.addProperty(CONFIG_VERSION_PROPERTY, PlatformHooks.PLATFORM_HELPER.getModVersion());
			json.addProperty(CONFIG_DATETIME_PROPERTY, LocalDateTime.now().format(DATETIME_FORMATTER));
			this.saveGeneralData(json);
			this.saveStructuresData(json);
			this.saveStructureSetsData(json);

			Files.createDirectories(configPath.getParent());
			Files.createFile(configPath);
			Files.writeString(configPath, gson.toJson(json));

			Structurify.getLogger().info("Structurify config saved");
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to save Structurify config");
			e.printStackTrace();

			try {
				Path possibleLatestBackupConfigPath = this.getLatestBackupConfigPath();

				if(possibleLatestBackupConfigPath != null) {
					Structurify.getLogger().error("Restoring Structurify backup config...");
					if (Files.exists(configPath)) {
						Files.delete(configPath);
					}

					Files.move(possibleLatestBackupConfigPath, configPath);
				}
			} catch (Exception fe) {
				Structurify.getLogger().error("Failed to restore Structurify backup config");
				fe.printStackTrace();
			}
		}
	}

	private void saveGeneralData(JsonObject json) {
		JsonObject general = new JsonObject();
		general.addProperty(MIN_STRUCTURE_DISTANCE_FROM_WORLD_CENTER_PROPERTY, this.minStructureDistanceFromWorldCenter);
		general.addProperty(DISABLE_ALL_STRUCTURES_PROPERTY, this.disableAllStructures);
		general.addProperty(ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY, this.enableGlobalSpacingAndSeparationModifier);
		general.addProperty(GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY, Math.round(this.globalSpacingAndSeparationModifier * 10.0) / 10.0);

		json.add(GENERAL_PROPERTY, general);
	}

	private void saveStructuresData(JsonObject json) {
		JsonArray structures = new JsonArray();

		this.structureData.entrySet().stream()
			.filter(structureDataEntry -> !structureDataEntry.getValue().isUsingDefaultValues())
			.forEach(structureDataEntry -> {
				JsonObject structure = new JsonObject();
				structure.addProperty(NAME_PROPERTY, structureDataEntry.getKey());
				structure.addProperty(IS_DISABLED_PROPERTY, structureDataEntry.getValue().isDisabled());

				structure.addProperty(ENABLE_FLATNESS_CHECK_PROPERTY, structureDataEntry.getValue().isFlatnessCheckEnabled());
				structure.addProperty(FLATNESS_CHECK_DISTANCE_PROPERTY, structureDataEntry.getValue().getFlatnessCheckDistance());
				structure.addProperty(FLATNESS_CHECK_THRESHOLD_PROPERTY, structureDataEntry.getValue().getFlatnessCheckThreshold());

				structure.addProperty(ENABLE_BIOME_CHECK_PROPERTY, structureDataEntry.getValue().isBiomeCheckEnabled());
				structure.addProperty(BIOME_CHECK_DISTANCE_PROPERTY, structureDataEntry.getValue().getBiomeCheckDistance());

				var whitelistedBiomes = new ArrayList<>(structureDataEntry.getValue().getBiomes());
				whitelistedBiomes.removeAll(structureDataEntry.getValue().getDefaultBiomes());
				JsonArray whitelistedBiomesJson = new JsonArray();
				whitelistedBiomes.stream().distinct().forEach(whitelistedBiomesJson::add);
				structure.add(WHITELISTED_BIOMES_PROPERTY, whitelistedBiomesJson);

				var blacklistedBiomes = new ArrayList<>(structureDataEntry.getValue().getDefaultBiomes());
				blacklistedBiomes.removeAll(structureDataEntry.getValue().getBiomes());
				JsonArray blacklistedBiomesJson = new JsonArray();
				blacklistedBiomes.stream().distinct().forEach(blacklistedBiomesJson::add);
				structure.add(BLACKLISTED_BIOMES_PROPERTY, blacklistedBiomesJson);

				structures.add(structure);
			});

		json.add(STRUCTURES_PROPERTY, structures);
	}

	private void saveStructureSetsData(JsonObject json) {
		JsonArray structureSets = new JsonArray();

		this.structureSetData.entrySet().stream()
			.filter(entry -> !entry.getValue().isUsingDefaultValues())
			.forEach(structureSetDataEntry -> {
				var structureSetData = structureSetDataEntry.getValue();
				var structureSetName = structureSetDataEntry.getKey();
				var overrideGlobalSpacingAndSeparationModifier = structureSetData.overrideGlobalSpacingAndSeparationModifier();
				var salt = structureSetData.getSalt();
				var frequency = structureSetData.getFrequency();
				var spacing =structureSetData.getSpacing();
				var separation = structureSetData.getSeparation();

				if((salt < StructureSetData.MIN_SALT || salt > StructureSetData.MAX_SALT) && salt != structureSetData.getDefaultSalt()) {
					Structurify.getLogger().info("Salt value for structure set {} is currently {}, which is invalid, value will be automatically corrected to {}.", structureSetName, salt, structureSetData.getDefaultSalt());
					salt = structureSetData.getDefaultSalt();
				}

				if(frequency < StructureSetData.MIN_FREQUENCY || frequency > StructureSetData.MAX_FREQUENCY) {
					Structurify.getLogger().info("Frequency value for structure set {} is currently {}, which is invalid, value will be automatically corrected to {}.", structureSetName, frequency, structureSetData.getDefaultFrequency());
					frequency = structureSetData.getDefaultFrequency();
				}

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
				specificStructureSpread.addProperty(NAME_PROPERTY, structureSetName);
				specificStructureSpread.addProperty(SALT_PROPERTY, salt);
				specificStructureSpread.addProperty(FREQUENCY_PROPERTY, frequency);
				specificStructureSpread.addProperty(OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY, overrideGlobalSpacingAndSeparationModifier);
				specificStructureSpread.addProperty(SPACING_PROPERTY, spacing);
				specificStructureSpread.addProperty(SEPARATION_PROPERTY, separation);
				structureSets.add(specificStructureSpread);
			});

		json.add(STRUCTURE_SETS_PROPERTY, structureSets);
	}

	private Path getBackupConfigPath() {
		String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		return Path.of(BACKUP_CONFIG_DIR.toString(), Structurify.MOD_ID + "_backup_"+ dateTime +".json");
	}

	private Path getLatestBackupConfigPath() {
		try {
			if (!Files.exists(BACKUP_CONFIG_DIR) || !Files.isDirectory(BACKUP_CONFIG_DIR)) {
				return null;
			}

			Optional<Path> latest = Files.list(BACKUP_CONFIG_DIR)
				.filter(path -> path.getFileName().toString().startsWith(BACKUP_PREFIX) && path.toString().endsWith(".json"))
				.max(Comparator.comparing(path -> {
					String timestamp = path.getFileName().toString()
						.replace(BACKUP_PREFIX, "")
						.replace(".json", "");
					try {
						return LocalDateTime.parse(timestamp, DATETIME_FORMATTER);
					} catch (Exception e) {
						return LocalDateTime.MIN;
					}
				}));

			return latest.orElse(null);

		} catch (IOException e) {
			Structurify.getLogger().error("Failed to load Structurify backup configs");
			e.printStackTrace();
			return null;
		}
	}
}