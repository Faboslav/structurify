package com.faboslav.structurify.common.config;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.*;
import com.faboslav.structurify.common.config.serialization.StructureDataSerializer;
import com.faboslav.structurify.common.config.serialization.StructureNamespaceDataSerializer;
import com.faboslav.structurify.common.config.serialization.StructureSetDataSerializer;
import com.faboslav.structurify.common.config.serialization.StructureTemplatePoolDataSerializer;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.platform.PlatformHooks;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.faboslav.structurify.common.registry.StructurifyTemplatePoolProvider;
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
	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

	public boolean isLoaded = false;
	public boolean isLoading = false;

	private final Path configPath = Path.of("config", Structurify.MOD_ID + ".json");
	public final Path configDumpPath = Path.of("config", Structurify.MOD_ID + "_dump.json");
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public boolean disableAllStructures = false;
	public boolean preventStructureOverlap = false;
	public boolean enableGlobalSpacingAndSeparationModifier = ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE;
	public double globalSpacingAndSeparationModifier = GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE;

	private final DebugData debugData = new DebugData();
	private Map<String, StructureNamespaceData> structureNamespaceData = new TreeMap<>();
	private Map<String, StructureData> structureData = new TreeMap<>();
	private Map<String, StructureSetData> structureSetData = new TreeMap<>();
	private Map<String, StructureTemplatePoolData> structureTemplatePoolsData = new TreeMap<>();

	public final static boolean ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE = false;
	public final static double GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE = 1.0D;

	public Map<String, StructureNamespaceData> getStructureNamespaceData() {
		return this.structureNamespaceData;
	}

	public Map<String, StructureData> getStructureData() {
		return this.structureData;
	}

	public Map<String, StructureSetData> getStructureSetData() {
		return this.structureSetData;
	}

	public Map<String, StructureTemplatePoolData> getStructureTemplatePoolsData() {
		return this.structureTemplatePoolsData;
	}

	public Map<String, StructureTemplatePoolData> getStructureTemplatePoolsDataForStructure(String structureId) {
		Set<String> structureTemplatePools = StructurifyTemplatePoolProvider.getStructureTemplatePoolIdsForStructure(structureId);
		return this.structureTemplatePoolsData.entrySet().stream()
			.filter(entry -> structureTemplatePools != null && structureTemplatePools.contains(entry.getKey()))
			.collect(java.util.stream.Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue
			));
	}

	public DebugData getDebugData() {
		return this.debugData;
	}

	public void create() {
		if (Files.exists(configPath)) {
			return;
		}

		this.save(false);
	}

	public void load(final LoadConfigEvent event) {
		this.load();
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
			this.structureTemplatePoolsData = WorldgenDataProvider.getStructureTemplatePools();

			if (!Files.exists(configPath)) {
				return;
			}

			String jsonString = Files.readString(configPath);
			JsonObject json = gson.fromJson(jsonString, JsonObject.class);

			StructurifyConfigSerializer.load(this, json);

			List<String> disabledStructures = Structurify.getConfig().getStructureData().entrySet()
				.stream()
				.filter(entry -> entry.getValue().isDisabled())
				.map(Map.Entry::getKey)
				.toList();

			if (!disabledStructures.isEmpty()) {
				Structurify.getLogger().info("Disabled {} structures: {}", disabledStructures.size(), disabledStructures);
			}

			List<String> changedStructures = Structurify.getConfig().getStructureData().entrySet()
				.stream()
				.filter(entry -> !entry.getValue().isUsingDefaultValues())
				.map(Map.Entry::getKey)
				.toList();

			List<String> changedStructureNamespaces = Structurify.getConfig().getStructureData().entrySet()
				.stream()
				.filter(entry -> !entry.getValue().isUsingDefaultValues())
				.map(Map.Entry::getKey)
				.toList();

			List<String> changedStructureSets = Structurify.getConfig().getStructureSetData().entrySet()
				.stream()
				.filter(entry -> !entry.getValue().isUsingDefaultValues())
				.map(Map.Entry::getKey)
				.toList();

			if (Structurify.getConfig().preventStructureOverlap) {
				Structurify.getLogger().info("Enabled structure overlap prevention");
			}

			if (Structurify.getConfig().enableGlobalSpacingAndSeparationModifier && Structurify.getConfig().globalSpacingAndSeparationModifier != 1.0D) {
				Structurify.getLogger().info("Enabled global spacing and separation modifier with value of {}", Structurify.getConfig().globalSpacingAndSeparationModifier);
			}

			if (!changedStructures.isEmpty()) {
				Structurify.getLogger().info("Changed settings of {} structures: {}", changedStructures.size(), changedStructures);
			}

			if (!changedStructureNamespaces.isEmpty()) {
				Structurify.getLogger().info("Changed settings of {} structure namespaces: {}", changedStructureNamespaces.size(), changedStructureNamespaces);
			}

			if (!changedStructureSets.isEmpty()) {
				Structurify.getLogger().info("Changed settings of {} structures sets: {}", changedStructureSets.size(), changedStructureSets);
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
		this.save(true);
	}

	public void save(boolean syncRegistries) {
		Structurify.getLogger().info("Saving Structurify config...");

		try {
			if (Files.exists(configPath)) {
				Structurify.getLogger().info("Creating Structurify backup config...");
				Path backupConfigPath = this.getBackupConfigPath();

				if (!Files.exists(BACKUP_CONFIG_DIR) || !Files.isDirectory(BACKUP_CONFIG_DIR)) {
					Files.createDirectories(BACKUP_CONFIG_DIR);
				}

				if (!Files.exists(backupConfigPath)) {
					Files.move(configPath, backupConfigPath);
					pruneBackupConfigFiles(5);
				}

				Structurify.getLogger().info("Structurify backup config created");
			}

			JsonObject json = StructurifyConfigSerializer.save(this);

			Files.createDirectories(configPath.getParent());
			Files.createFile(configPath);
			Files.writeString(configPath, gson.toJson(json));

			Structurify.getLogger().info("Structurify config saved");

			if(syncRegistries) {
				Structurify.getLogger().info("Syncing changes to registries...");
				UpdateRegistriesEvent.EVENT.invoke(new UpdateRegistriesEvent(StructurifyRegistryManagerProvider.getRegistryManager()));
				Structurify.getLogger().info("Registries synced");
			}
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

			JsonObject json = StructurifyConfigSerializer.save(this, false);

			Files.createDirectories(configDumpPath.getParent());
			Files.createFile(configDumpPath);
			Files.writeString(configDumpPath, gson.toJson(json));

			Structurify.getLogger().info("Structurify config successfully dumped");
		} catch (Exception e) {
			Structurify.getLogger().error("Failed to dump Structurify config");
			e.printStackTrace();
		}
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

	private void pruneBackupConfigFiles(int keep) {
		try {
			if (!Files.exists(BACKUP_CONFIG_DIR) || !Files.isDirectory(BACKUP_CONFIG_DIR)) {
				return;
			}

			List<Path> backups = Files.list(BACKUP_CONFIG_DIR)
				.filter(path -> {
					String name = path.getFileName().toString();
					return name.startsWith(BACKUP_PREFIX) && name.endsWith(".json");
				})
				.sorted((a, b) -> {
					String ta = a.getFileName().toString()
						.replace(BACKUP_PREFIX, "")
						.replace(".json", "");
					String tb = b.getFileName().toString()
						.replace(BACKUP_PREFIX, "")
						.replace(".json", "");
					try {
						LocalDateTime da = LocalDateTime.parse(ta, DATETIME_FORMATTER);
						LocalDateTime db = LocalDateTime.parse(tb, DATETIME_FORMATTER);
						return db.compareTo(da);
					} catch (Exception e) {
						return 0;
					}
				})
				.toList();

			if (backups.size() <= keep) {
				return;
			}

			for (int i = keep; i < backups.size(); i++) {
				try {
					Files.deleteIfExists(backups.get(i));
				} catch (IOException ex) {
					Structurify.getLogger().warn("Failed to delete old backup {}", backups.get(i).getFileName().toString());
				}
			}
		} catch (IOException e) {
			Structurify.getLogger().error("Failed to prune Structurify backup configs");
			e.printStackTrace();
		}
	}
}