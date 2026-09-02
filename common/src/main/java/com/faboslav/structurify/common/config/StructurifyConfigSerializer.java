package com.faboslav.structurify.common.config;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import com.faboslav.structurify.common.config.serialization.StructureDataSerializer;
import com.faboslav.structurify.common.config.serialization.StructureNamespaceDataSerializer;
import com.faboslav.structurify.common.config.serialization.StructureSetDataSerializer;
import com.faboslav.structurify.common.config.serialization.StructureTemplatePoolDataSerializer;
import com.faboslav.structurify.common.platform.PlatformHooks;
import com.google.common.hash.Hashing;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;

public final class StructurifyConfigSerializer
{
	public static final String CONFIG_VERSION_PROPERTY = "config_version";
	public static final String CONFIG_DATETIME_PROPERTY = "config_datetime";
	public static final String GENERAL_PROPERTY = "general";
	public static final String DISABLE_ALL_STRUCTURES_PROPERTY = "disable_all_structures";
	public static final String PREVENT_STRUCTURE_OVERLAP_PROPERTY = "prevent_structure_overlap";
	public static final String ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY = "enable_global_spacing_and_separation_modifier";
	public static final String GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY = "global_spacing_and_separation_modifier";

	public static final String STRUCTURES_PROPERTY = "structures";
	public static final String STRUCTURE_NAMESPACES_PROPERTY = "structure_namespaces";
	public static final String STRUCTURE_SETS_PROPERTY = "structure_sets";
	public static final String STRUCTURE_TEMPLATE_POOLS_PROPERTY = "structure_template_pools";

	public static void load(StructurifyConfig config, JsonObject json) {
		loadGeneral(config, json);
		loadStructureNamespaces(config, json);
		loadStructures(config, json);
		loadStructureSets(config, json);
		loadStructureTemplatePools(config, json);
	}

	private static void loadGeneral(StructurifyConfig config, JsonObject json) {
		if (!json.has(GENERAL_PROPERTY)) {
			return;
		}

		var general = json.getAsJsonObject(GENERAL_PROPERTY);

		if (general.has(DISABLE_ALL_STRUCTURES_PROPERTY)) {
			config.disableAllStructures = general.get(DISABLE_ALL_STRUCTURES_PROPERTY).getAsBoolean();
		}

		if (general.has(PREVENT_STRUCTURE_OVERLAP_PROPERTY)) {
			config.preventStructureOverlap = general.get(PREVENT_STRUCTURE_OVERLAP_PROPERTY).getAsBoolean();
		}

		if (general.has(ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY)) {
			config.enableGlobalSpacingAndSeparationModifier = general.get(ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY).getAsBoolean();
		}

		if (general.has(GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY)) {
			config.globalSpacingAndSeparationModifier = Math.round(general.get(GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY).getAsDouble() * 10.0) / 10.0;
		}
	}

	private static void loadStructureNamespaces(StructurifyConfig config, JsonObject json) {
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

			if (!config.getStructureNamespaceData().containsKey(structureNamespaceJson.get(StructureNamespaceDataSerializer.NAME_PROPERTY).getAsString())) {
				Structurify.getLogger().info("Found invalid structure namespace identifier of \"{}\", skipping.", structureNamespaceJson.get(StructureNamespaceDataSerializer.NAME_PROPERTY).getAsString());
				continue;
			}

			StructureNamespaceData structureNamespaceData = config.getStructureNamespaceData().get(structureNamespaceJson.get(StructureNamespaceDataSerializer.NAME_PROPERTY).getAsString());

			if (structureNamespaceData == null) {
				continue;
			}

			StructureNamespaceDataSerializer.load(structureNamespaceJson, structureNamespaceData);
		}
	}

	private static void loadStructures(StructurifyConfig config, JsonObject json) {
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

			if (!config.getStructureData().containsKey(structureJson.get(StructureDataSerializer.NAME_PROPERTY).getAsString())) {
				Structurify.getLogger().info("Found invalid structure identifier of \"{}\", skipping.", structureJson.get(StructureDataSerializer.NAME_PROPERTY).getAsString());
				continue;
			}

			StructureData structureData = config.getStructureData().get(structureJson.get(StructureDataSerializer.NAME_PROPERTY).getAsString());

			if (structureData == null) {
				continue;
			}

			StructureDataSerializer.load(structureJson, structureData);
		}
	}

	private static void loadStructureSets(StructurifyConfig config, JsonObject json) {
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

			if (!config.getStructureSetData().containsKey(structureSetName)) {
				Structurify.getLogger().info("Found invalid structure set identifier of \"{}\", skipping.", structureSetName);
				continue;
			}

			var structureSetData = config.getStructureSetData().get(structureSetName);
			StructureSetDataSerializer.load(structureSetJson, structureSetData);
		}
	}

	private static void loadStructureTemplatePools(StructurifyConfig config, JsonObject json) {
		if (!json.has(STRUCTURE_TEMPLATE_POOLS_PROPERTY)) {
			return;
		}

		var structureTemplatePools = json.getAsJsonArray(STRUCTURE_TEMPLATE_POOLS_PROPERTY);

		for (JsonElement structureTemplatePool : structureTemplatePools) {
			var structureTemplatePoolJson = structureTemplatePool.getAsJsonObject();

			if (!structureTemplatePoolJson.has(StructureSetDataSerializer.NAME_PROPERTY)) {
				Structurify.getLogger().info("Found invalid structure template pool entry, skipping.");
				continue;
			}

			var structureTemplatePoolName = structureTemplatePoolJson.get(StructureSetDataSerializer.NAME_PROPERTY).getAsString();

			if (!config.getStructureTemplatePoolsData().containsKey(structureTemplatePoolName)) {
				Structurify.getLogger().info("Found invalid structure template pool identifier of \"{}\", skipping.", structureTemplatePoolName);
				continue;
			}

			var structureTemplatePoolData = config.getStructureTemplatePoolsData().get(structureTemplatePoolName);
			StructureTemplatePoolDataSerializer.load(structureTemplatePoolJson, structureTemplatePoolData);
		}
	}

	public static String computeConfigHash(StructurifyConfig config) {
		return hashConfigJson(save(config, true));
	}

	public static String hashConfigJson(JsonObject json) {
		JsonObject copy = json.deepCopy();
		copy.remove(CONFIG_VERSION_PROPERTY);
		copy.remove(CONFIG_DATETIME_PROPERTY);
		return Hashing.sha256().hashString(copy.toString(), StandardCharsets.UTF_8).toString();
	}

	public static JsonObject save(StructurifyConfig config) {
		return save(config, true);
	}

	public static JsonObject save(StructurifyConfig config, boolean saveOnlyChanged) {
		JsonObject json = new JsonObject();

		json.addProperty(CONFIG_VERSION_PROPERTY, PlatformHooks.PLATFORM_HELPER.getModVersion());
		json.addProperty(CONFIG_DATETIME_PROPERTY, LocalDateTime.now().format(StructurifyConfig.DATETIME_FORMATTER));

		saveGeneralData(config, json);
		saveStructureNamespacesData(config, json, saveOnlyChanged);
		saveStructuresData(config, json, saveOnlyChanged);
		saveStructureSetsData(config, json, saveOnlyChanged);
		saveStructureTemplatePoolsData(config, json, saveOnlyChanged);

		return json;
	}

	private static void saveGeneralData(StructurifyConfig config, JsonObject json) {
		JsonObject general = new JsonObject();
		general.addProperty(DISABLE_ALL_STRUCTURES_PROPERTY, config.disableAllStructures);
		general.addProperty(PREVENT_STRUCTURE_OVERLAP_PROPERTY, config.preventStructureOverlap);
		general.addProperty(ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY, config.enableGlobalSpacingAndSeparationModifier);
		general.addProperty(GLOBAL_SPACING_AND_SEPARATION_MODIFIER_PROPERTY, Math.round(config.globalSpacingAndSeparationModifier * 10.0) / 10.0);

		json.add(GENERAL_PROPERTY, general);
	}

	private static void saveStructureNamespacesData(
		StructurifyConfig config,
		JsonObject json,
		boolean saveOnlyChanged
	) {
		JsonArray structureNamespaces = new JsonArray();

		config.getStructureNamespaceData().entrySet().stream()
			.filter(entry -> !saveOnlyChanged || !entry.getValue().isUsingDefaultValues())
			.forEach(structureNamespaceDataEntry -> {
				StructureNamespaceDataSerializer.save(structureNamespaces, structureNamespaceDataEntry.getKey(), structureNamespaceDataEntry.getValue(), saveOnlyChanged);
			});

		json.add(STRUCTURE_NAMESPACES_PROPERTY, structureNamespaces);
	}

	private static void saveStructuresData(StructurifyConfig config, JsonObject json, boolean saveOnlyChanged) {
		JsonArray structures = new JsonArray();

		config.getStructureData().entrySet().stream()
			.filter(entry -> !saveOnlyChanged || !entry.getValue().isUsingDefaultValues())
			.forEach(structureDataEntry -> {
				StructureDataSerializer.save(structures, structureDataEntry.getKey(), structureDataEntry.getValue(), saveOnlyChanged);
			});

		json.add(STRUCTURES_PROPERTY, structures);
	}

	private static void saveStructureSetsData(StructurifyConfig config, JsonObject json, boolean saveOnlyChanged) {
		JsonArray structureSets = new JsonArray();
		var structureSetSalts = new HashMap<Integer, String>();

		config.getStructureSetData().entrySet().stream()
			.filter(entry -> !saveOnlyChanged || !entry.getValue().isUsingDefaultValues())
			.forEach(structureSetDataEntry -> {
				var structureSetName = structureSetDataEntry.getKey();
				var structureSetData = structureSetDataEntry.getValue();
				var salt = structureSetData.getSalt();

				if (structureSetSalts.containsKey(salt)) {
					Structurify.getLogger().warn("Salt value for structure set {} is currently {}, which is already being used by {} structure set.", structureSetName, salt, structureSetSalts.get(salt));
				} else {
					structureSetSalts.put(structureSetData.getSalt(), structureSetName);
				}

				StructureSetDataSerializer.save(structureSets, structureSetName, structureSetData, saveOnlyChanged);
			});

		json.add(STRUCTURE_SETS_PROPERTY, structureSets);
	}

	private static void saveStructureTemplatePoolsData(
		StructurifyConfig config,
		JsonObject json,
		boolean saveOnlyChanged
	) {
		JsonArray structureTemplatePools = new JsonArray();

		config.getStructureTemplatePoolsData().entrySet().stream()
			.filter(entry -> !saveOnlyChanged || !entry.getValue().isUsingDefaultValues())
			.forEach(structureTemplatePoolDataEntry -> {
				var structureTemplatePoolName = structureTemplatePoolDataEntry.getKey();
				var structureTemplatePoolData = structureTemplatePoolDataEntry.getValue();

				StructureTemplatePoolDataSerializer.save(structureTemplatePools, structureTemplatePoolName, structureTemplatePoolData, saveOnlyChanged);
			});

		json.add(STRUCTURE_TEMPLATE_POOLS_PROPERTY, structureTemplatePools);
	}
}
