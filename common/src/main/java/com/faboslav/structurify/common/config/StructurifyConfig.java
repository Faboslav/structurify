package com.faboslav.structurify.common.config;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.*;
import com.faboslav.structurify.common.config.serialization.*;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.platform.PlatformHooks;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.google.gson.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class StructurifyConfig
{
	private static final Path BACKUP_CONFIG_DIR = Path.of("config/structurify");
	private static final String BACKUP_PREFIX = Structurify.MOD_ID + "_backup_";
	private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

	public boolean isLoaded = false;
	public boolean isLoading = false;

	private final Path configPath = Path.of("config", Structurify.MOD_ID + ".json");
	public final Path configDumpPath = Path.of("config", Structurify.MOD_ID + "_dump.json");
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public boolean disableAllStructures = false;
	public boolean preventStructureOverlap = false;
	public int minStructureDistanceFromWorldCenter = 0;
	public boolean enableGlobalSpacingAndSeparationModifier = ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE;
	public double globalSpacingAndSeparationModifier = GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE;

	private DebugData debugData = new DebugData();
	private Map<String, StructureNamespaceData> structureNamespaceData = new TreeMap<>();
	private Map<String, StructureData> structureData = new TreeMap<>();
	private Map<String, StructureSetData> structureSetData = new TreeMap<>();

	public final static boolean ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE = false;
	public final static double GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE = 1.0D;

	private static final String CONFIG_VERSION_PROPERTY = "config_version";
	private static final String CONFIG_DATETIME_PROPERTY = "config_datetime";
	private static final String GENERAL_PROPERTY = "general";
	private static final String MIN_STRUCTURE_DISTANCE_FROM_WORLD_CENTER_PROPERTY = "min_structure_distance_from_world_center";
	private static final String DISABLE_ALL_STRUCTURES_PROPERTY = "disable_all_structures";
	private static final String PREVENT_STRUCTURE_OVERLAP_PROPERTY = "prevent_structure_overlap";
	private static final String ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY = "enable_global_spacing_and_separation_modifier";
	private static final String GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY = "global_spacing_and_separation_modifier";

	private static final String STRUCTURES_PROPERTY = "structures";
	private static final String STRUCTURE_NAMESPACES_PROPERTY = "structure_namespaces";
	private static final String STRUCTURE_SETS_PROPERTY = "structure_sets";


	public Map<String, StructureNamespaceData> getStructureNamespaceData() {
		return this.structureNamespaceData;
	}

	public Map<String, StructureData> getStructureData() {
		return this.structureData;
	}

	public Map<String, StructureSetData> getStructureSetData() {
		return this.structureSetData;
	}

	public DebugData getDebugData() {
		return this.debugData;
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
			this.structureNamespaceData = WorldgenDataProvider.getStructureNamespaces();
			this.structureData = WorldgenDataProvider.getStructures();
			this.structureSetData = WorldgenDataProvider.getStructureSets();

			if (!Files.exists(configPath)) {
				return;
			}

			String jsonString = Files.readString(configPath);
			JsonObject json = gson.fromJson(jsonString, JsonObject.class);

			this.loadGeneral(json);
			this.loadStructureNamespaces(json);
			this.loadStructures(json);
			this.loadStructureSets(json);

			Structurify.getLogger().info("Structurify config loaded");
			this.isLoaded = true;
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to load Structurify config");
			e.printStackTrace();
		} finally {
			this.isLoading = false;
		}
	}

	private void loadGeneral(JsonObject json) {
		if (!json.has(GENERAL_PROPERTY)) {
			return;
		}

		var general = json.getAsJsonObject(GENERAL_PROPERTY);

		if (general.has(DISABLE_ALL_STRUCTURES_PROPERTY)) {
			this.disableAllStructures = general.get(DISABLE_ALL_STRUCTURES_PROPERTY).getAsBoolean();
		}

		if (general.has(PREVENT_STRUCTURE_OVERLAP_PROPERTY)) {
			this.preventStructureOverlap = general.get(PREVENT_STRUCTURE_OVERLAP_PROPERTY).getAsBoolean();
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

	private void loadStructureNamespaces(JsonObject json) {
		if (!json.has(STRUCTURE_NAMESPACES_PROPERTY)) {
			return;
		}

		var structureNamespaces = json.getAsJsonArray(STRUCTURE_NAMESPACES_PROPERTY);

		for (JsonElement structureNamespace : structureNamespaces) {
			var structureNamespaceJson = structureNamespace.getAsJsonObject();

			if (!structureNamespaceJson.has(StructureNamespaceDataSerializer.NAME_PROPERTY)) {
				Structurify.getLogger().info("Found invalid structure namespace entry, skipping.");
				continue;
			}

			if (!this.structureNamespaceData.containsKey(structureNamespaceJson.get(StructureNamespaceDataSerializer.NAME_PROPERTY).getAsString())) {
				Structurify.getLogger().info("Found invalid structure namespace identifier of \"{}\", skipping.", structureNamespaceJson.get(StructureNamespaceDataSerializer.NAME_PROPERTY).getAsString());
				continue;
			}

			StructureNamespaceData structureNamespaceData = this.structureNamespaceData.get(structureNamespaceJson.get(StructureNamespaceDataSerializer.NAME_PROPERTY).getAsString());

			if(structureNamespaceData == null) {
				continue;
			}

			StructureNamespaceDataSerializer.load(structureNamespaceJson, structureNamespaceData);
		}
	}

	private void loadStructures(JsonObject json) {
		if (!json.has(STRUCTURES_PROPERTY)) {
			return;
		}

		var structures = json.getAsJsonArray(STRUCTURES_PROPERTY);

		for (JsonElement structure : structures) {
			var structureJson = structure.getAsJsonObject();

			if (!structureJson.has(StructureDataSerializer.NAME_PROPERTY)) {
				Structurify.getLogger().info("Found invalid structure entry, skipping.");
				continue;
			}

			if (!this.structureData.containsKey(structureJson.get(StructureDataSerializer.NAME_PROPERTY).getAsString())) {
				Structurify.getLogger().info("Found invalid structure identifier of \"{}\", skipping.", structureJson.get(StructureDataSerializer.NAME_PROPERTY).getAsString());
				continue;
			}

			StructureData structureData = this.structureData.get(structureJson.get(StructureDataSerializer.NAME_PROPERTY).getAsString());

			if(structureData == null) {
				continue;
			}

			StructureDataSerializer.load(structureJson, structureData);
		}
	}

	private void loadStructureSets(JsonObject json) {
		if (!json.has(STRUCTURE_SETS_PROPERTY)) {
			return;
		}

		var structureSets = json.getAsJsonArray(STRUCTURE_SETS_PROPERTY);

		for (JsonElement structureSet : structureSets) {
			var structureSetJson = structureSet.getAsJsonObject();

			if (!structureSetJson.has(StructureSetDataSerializer.NAME_PROPERTY)) {
				Structurify.getLogger().info("Found invalid structure set entry, skipping.");
				continue;
			}

			var structureSetName = structureSetJson.get(StructureSetDataSerializer.NAME_PROPERTY).getAsString();

			if (!this.structureSetData.containsKey(structureSetName)) {
				Structurify.getLogger().info("Found invalid structure set identifier of \"{}\", skipping.", structureSetName);
				continue;
			}

			var structureSetData = this.structureSetData.get(structureSetName);
			StructureSetDataSerializer.load(structureSetJson, structureSetData);
		}
	}

	public void save() {
		Structurify.getLogger().info("Saving Structurify config...");

		try {
			if (Files.exists(configPath)) {
				// TODO delete all old backups here
				Path backupConfigPath = this.getBackupConfigPath();

				if (!Files.exists(BACKUP_CONFIG_DIR) || !Files.isDirectory(BACKUP_CONFIG_DIR)) {
					Files.createDirectories(BACKUP_CONFIG_DIR);
				}

				if (!Files.exists(backupConfigPath)) {
					Files.move(configPath, backupConfigPath);
				}
			}

			JsonObject json = new JsonObject();

			json.addProperty(CONFIG_VERSION_PROPERTY, PlatformHooks.PLATFORM_HELPER.getModVersion());
			json.addProperty(CONFIG_DATETIME_PROPERTY, LocalDateTime.now().format(DATETIME_FORMATTER));
			this.saveGeneralData(json);
			this.saveStructureNamespacesData(json, true);
			this.saveStructuresData(json, true);
			this.saveStructureSetsData(json, true);

			Files.createDirectories(configPath.getParent());
			Files.createFile(configPath);
			Files.writeString(configPath, gson.toJson(json));

			Structurify.getLogger().info("Structurify config saved");

			Structurify.getLogger().info("Syncing changes to registries...");
			UpdateRegistriesEvent.EVENT.invoke(new UpdateRegistriesEvent(StructurifyRegistryManagerProvider.getRegistryManager()));
			Structurify.getLogger().info("Registries synced");
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to save Structurify config");
			e.printStackTrace();

			try {
				Path possibleLatestBackupConfigPath = this.getLatestBackupConfigPath();

				if (possibleLatestBackupConfigPath != null) {
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

	public void dump() {
		Structurify.getLogger().info("Dumping Structurify config...");

		try {
			if (Files.exists(configDumpPath)) {
				Files.delete(configDumpPath);
			}

			JsonObject json = new JsonObject();

			json.addProperty(CONFIG_VERSION_PROPERTY, PlatformHooks.PLATFORM_HELPER.getModVersion());
			json.addProperty(CONFIG_DATETIME_PROPERTY, LocalDateTime.now().format(DATETIME_FORMATTER));
			this.saveGeneralData(json);
			this.saveStructureNamespacesData(json, false);
			this.saveStructuresData(json, false);
			this.saveStructureSetsData(json, false);

			Files.createDirectories(configDumpPath.getParent());
			Files.createFile(configDumpPath);
			Files.writeString(configDumpPath, gson.toJson(json));

			Structurify.getLogger().info("Structurify config successfully dumped");
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to dump Structurify config");
			e.printStackTrace();
		}
	}

	private void saveGeneralData(JsonObject json) {
		JsonObject general = new JsonObject();
		general.addProperty(MIN_STRUCTURE_DISTANCE_FROM_WORLD_CENTER_PROPERTY, this.minStructureDistanceFromWorldCenter);
		general.addProperty(DISABLE_ALL_STRUCTURES_PROPERTY, this.disableAllStructures);
		general.addProperty(PREVENT_STRUCTURE_OVERLAP_PROPERTY, this.preventStructureOverlap);
		general.addProperty(ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY, this.enableGlobalSpacingAndSeparationModifier);
		general.addProperty(GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY, Math.round(this.globalSpacingAndSeparationModifier * 10.0) / 10.0);

		json.add(GENERAL_PROPERTY, general);
	}

	private void saveStructureNamespacesData(JsonObject json, boolean saveOnlyChanged) {
		JsonArray structureNamespaces = new JsonArray();

		this.structureNamespaceData.entrySet().stream()
			.filter(entry -> !saveOnlyChanged || !entry.getValue().isUsingDefaultValues())
			.forEach(structureNamespaceDataEntry -> {
				StructureNamespaceDataSerializer.save(structureNamespaces, structureNamespaceDataEntry.getKey(), structureNamespaceDataEntry.getValue());
			});

		json.add(STRUCTURE_NAMESPACES_PROPERTY, structureNamespaces);
	}

	private void saveStructuresData(JsonObject json, boolean saveOnlyChanged) {
		JsonArray structures = new JsonArray();

		this.structureData.entrySet().stream()
			.filter(entry -> !saveOnlyChanged || !entry.getValue().isUsingDefaultValues())
			.forEach(structureDataEntry -> {
				StructureDataSerializer.save(structures, structureDataEntry.getKey(), structureDataEntry.getValue());
			});

		json.add(STRUCTURES_PROPERTY, structures);
	}

	private void saveStructureSetsData(JsonObject json, boolean saveOnlyChanged) {
		JsonArray structureSets = new JsonArray();
		var structureSetSalts = new HashMap<Integer, String>();

		this.structureSetData.entrySet().stream()
			.filter(entry -> !saveOnlyChanged || entry.getValue().isUsingDefaultValues())
			.forEach(structureSetDataEntry -> {
				var structureSetName = structureSetDataEntry.getKey();
				var structureSetData = structureSetDataEntry.getValue();
				var salt = structureSetData.getSalt();

				if(structureSetSalts.containsKey(salt)) {
					Structurify.getLogger().warn("Salt value for structure set {} is currently {}, which is already being used by {} structure set.", structureSetName, salt, structureSetSalts.get(salt));
				} else {
					structureSetSalts.put(structureSetData.getSalt(), structureSetName);
				}

				StructureSetDataSerializer.save(structureSets, structureSetName, structureSetData);
			});

		json.add(STRUCTURE_SETS_PROPERTY, structureSets);
	}

	private Path getBackupConfigPath() {
		String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
		return Path.of(BACKUP_CONFIG_DIR.toString(), Structurify.MOD_ID + "_backup_" + dateTime + ".json");
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