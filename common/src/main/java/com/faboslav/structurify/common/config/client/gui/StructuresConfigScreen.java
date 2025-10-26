package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.StructureButtonControllerBuilder;
import com.faboslav.structurify.common.config.client.api.option.HolderOption;
import com.faboslav.structurify.common.config.client.api.option.InvisibleOptionGroup;
import com.faboslav.structurify.common.config.client.gui.structure.BiomeCheckOptions;
import com.faboslav.structurify.common.config.client.gui.structure.DistanceFromWorldCenterOptions;
import com.faboslav.structurify.common.config.client.gui.structure.FlatnessCheckOptions;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.faboslav.structurify.common.util.LanguageUtil;
import com.faboslav.structurify.common.util.YACLUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked"})
public final class StructuresConfigScreen
{
	private final static List<Option<Boolean>> structureOptions = new ArrayList<>();
	private static HolderOption<Option<Integer>, Option<Integer>> globalDistanceFromWorldCenterOption = null;
	private static Option<Boolean> enableGlobalFlatnessCheckOption = null;
	private static Option<Boolean> enableGlobalBiomeCheckOption = null;
	private final static List<Option<Boolean>> overrideDistanceFromWorldCenterOptions = new ArrayList<>();
	private final static List<Option<Boolean>> overrideFlatnessCheckOptions = new ArrayList<>();
	private final static List<Option<Boolean>> overrideBiomeCheckOptions = new ArrayList<>();
	private final static List<Option<Boolean>> enableFlatnessCheckOptions = new ArrayList<>();
	private final static List<Option<Boolean>> enableBiomeCheckOptions = new ArrayList<>();

	public static void createStructuresTab(YetAnotherConfigLib.Builder yacl, StructurifyConfig config) {
		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structures_category.title"))
			.tooltip(Component.translatable("gui.structurify.structures_category.description"));

		addGlobalSettings(structureCategoryBuilder, config);
		addStructures(structureCategoryBuilder, config);

		globalDistanceFromWorldCenterOption.addListener((opt, enableGlobalFlatnessCheck) -> {
			for (var overrideDistanceFromWorldCenterOption : overrideDistanceFromWorldCenterOptions) {
				overrideDistanceFromWorldCenterOption.setAvailable(!globalDistanceFromWorldCenterOption.isPendingValueDefault());
			}
		});

		enableGlobalFlatnessCheckOption.addListener((opt, enableGlobalFlatnessCheck) -> {
			for (int i = 0; i < overrideFlatnessCheckOptions.size(); i++) {
				var overrideFlatnessCheckOption = overrideFlatnessCheckOptions.get(i);
				overrideFlatnessCheckOption.setAvailable(enableGlobalFlatnessCheck);
				overrideFlatnessCheckOption.requestSetDefault();

				var enableFlatnessCheckOption = enableFlatnessCheckOptions.get(i);
				enableFlatnessCheckOption.setAvailable(!overrideFlatnessCheckOption.available());
				enableFlatnessCheckOption.requestSetDefault();
			}
		});

		enableGlobalBiomeCheckOption.addListener((opt, enableGlobalBiomeCheck) -> {
			for (int i = 0; i < overrideBiomeCheckOptions.size(); i++) {
				var overrideBiomeCheckOption = overrideBiomeCheckOptions.get(i);
				overrideBiomeCheckOption.setAvailable(enableGlobalBiomeCheck);
				overrideBiomeCheckOption.requestSetDefault();

				var enableBiomeCheckOption = enableBiomeCheckOptions.get(i);
				enableBiomeCheckOption.setAvailable(!overrideBiomeCheckOption.available());
				enableBiomeCheckOption.requestSetDefault();
			}
		});

		yacl.category(structureCategoryBuilder.build());
	}

	private static void addGlobalSettings(ConfigCategory.Builder structureCategoryBuilder, StructurifyConfig config) {
		var globalStructuresGroupBuilder = OptionGroup.createBuilder()
			.name(Component.translatable("gui.structurify.structures.global.title").withStyle(style -> style.withUnderlined(true)))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.global.description")));

		globalStructuresGroupBuilder.option(LabelOption.create(Component.translatable("gui.structurify.structures.structures.structure.title").withStyle(style -> style.withBold(true))));

		var disableAllStructuresOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.disable_all_structures.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.disable_all_structures.description")))
			.binding(
				false,
				() -> config.disableAllStructures,
				disableAllStructures -> config.disableAllStructures = disableAllStructures
			)
			.controller(opt -> BooleanControllerBuilder.create(opt).valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes").withStyle(style -> style.withColor(ChatFormatting.RED)):Component.translatable("gui.structurify.label.no").withStyle(style -> style.withColor(ChatFormatting.GREEN)))).build();

		disableAllStructuresOption.addListener((opt, disableAllStructures) -> {
			for (var structureOption : structureOptions) {
				structureOption.requestSet(!disableAllStructures);
			}
		});

		globalStructuresGroupBuilder.option(disableAllStructuresOption);

		var preventStructureOverlapOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.prevent_structure_overlap.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.prevent_structure_overlap.description")))
			.binding(
				false,
				() -> config.preventStructureOverlap,
				preventStructureOverlap -> config.preventStructureOverlap = preventStructureOverlap
			)
			.controller(opt -> BooleanControllerBuilder.create(opt).valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes").withStyle(style -> style.withColor(ChatFormatting.GREEN)):Component.translatable("gui.structurify.label.no").withStyle(style -> style.withColor(ChatFormatting.RED)))).build();

		globalStructuresGroupBuilder.option(preventStructureOverlapOption);

		var globalDistanceFromWorldCenterOptions = DistanceFromWorldCenterOptions.addDistanceFromWorldCenterOptions(globalStructuresGroupBuilder, config, "global");
		globalDistanceFromWorldCenterOption = (HolderOption<Option<Integer>, Option<Integer>>) globalDistanceFromWorldCenterOptions.get(DistanceFromWorldCenterOptions.DISTANCE_FROM_WORLD_CENTER_OPTION_NAME);

		var globalFlatnessCheckOptions = FlatnessCheckOptions.addFlatnessCheckOptions(globalStructuresGroupBuilder, config, "global");
		enableGlobalFlatnessCheckOption = (Option<Boolean>) globalFlatnessCheckOptions.get(FlatnessCheckOptions.FLATNESS_CHECK_IS_ENABLED_OPTION_NAME);

		var globalBiomeCheckOptions = BiomeCheckOptions.addBiomeCheckOptions(structureCategoryBuilder, globalStructuresGroupBuilder, config, "global");
		OptionGroup blackListedBiomesOption = (OptionGroup) globalBiomeCheckOptions.get(BiomeCheckOptions.BIOME_CHECK_BLACKLISTED_BIOMES_OPTION_NAME);
		enableGlobalBiomeCheckOption = (Option<Boolean>) globalBiomeCheckOptions.get(BiomeCheckOptions.BIOME_CHECK_IS_ENABLED_OPTION_NAME);

		structureCategoryBuilder.group(globalStructuresGroupBuilder.build());
		structureCategoryBuilder.group(blackListedBiomesOption);
	}

	private static void addStructures(ConfigCategory.Builder structureCategoryBuilder, StructurifyConfig config) {
		var structures = WorldgenDataProvider.getStructures();
		var structureGroups = new HashMap<String, HashMap<ResourceLocation, StructureData>>();

		for (Map.Entry<String, StructureData> entry : structures.entrySet()) {
			String structureStringId = entry.getKey();
			ResourceLocation structureId = Structurify.makeNamespacedId(structureStringId);
			String structureNamespace = structureId.getNamespace();
			StructureData structureData = entry.getValue();

			if(!structureGroups.containsKey(structureNamespace)) {
				structureGroups.put(structureNamespace, new HashMap<>());
			}

			structureGroups.get(structureNamespace).put(structureId, structureData);
		}

		var biomeRegistry = StructurifyRegistryManagerProvider.getBiomeRegistry();

		for (Map.Entry<String, HashMap<ResourceLocation, StructureData>> structureGroup : structureGroups.entrySet()) {
			String structureNamespace = structureGroup.getKey();
			var namespaceStructures = structureGroup.getValue();

			var invisibleGroup = new InvisibleOptionGroup.Builder().name(Component.literal(""));
			invisibleGroup.option(YACLUtil.createEmptyLabelOption());
			structureCategoryBuilder.group(invisibleGroup.build());

			OptionGroup.Builder namespaceGroupBuilder = OptionGroup.createBuilder()
				.name(Component.translatable("gui.structurify.structures.structures_group.title", LanguageUtil.translateId(null, structureNamespace).getString()).withStyle(style -> style.withUnderlined(true)))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structures_group.description", structureNamespace)));
			namespaceGroupBuilder.option(LabelOption.create(Component.translatable("gui.structurify.structures.structures.structure.title").withStyle(style -> style.withBold(true))));

			for (var namespaceStructure : namespaceStructures.entrySet()) {
				var structureData = namespaceStructure.getValue();
				var structureStringId = namespaceStructure.getKey().toString();
				var structureOption = addStructure(structureData, structureStringId, biomeRegistry);
				namespaceGroupBuilder.option(structureOption);
				structureOptions.add(structureOption);
			}

			var namespaceDistanceFromWorldCenterOptions = DistanceFromWorldCenterOptions.addDistanceFromWorldCenterOptions(namespaceGroupBuilder, config, structureNamespace);
			overrideDistanceFromWorldCenterOptions.add((Option<Boolean>) namespaceDistanceFromWorldCenterOptions.get(DistanceFromWorldCenterOptions.OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME));

			var namespaceFlatnessCheckOptions = FlatnessCheckOptions.addFlatnessCheckOptions(namespaceGroupBuilder, config, structureNamespace);
			overrideFlatnessCheckOptions.add((Option<Boolean>) namespaceFlatnessCheckOptions.get(FlatnessCheckOptions.OVERRIDE_GLOBAL_FLATNESS_CHECK_OPTION_NAME));
			enableFlatnessCheckOptions.add((Option<Boolean>) namespaceFlatnessCheckOptions.get(FlatnessCheckOptions.FLATNESS_CHECK_IS_ENABLED_OPTION_NAME));

			var namespaceBiomeCheckOptions = BiomeCheckOptions.addBiomeCheckOptions(structureCategoryBuilder, namespaceGroupBuilder, config, structureNamespace);
			OptionGroup blackListedBiomesOption = (OptionGroup) namespaceBiomeCheckOptions.get(BiomeCheckOptions.BIOME_CHECK_BLACKLISTED_BIOMES_OPTION_NAME);
			structureCategoryBuilder.group(namespaceGroupBuilder.build());
			structureCategoryBuilder.group(blackListedBiomesOption);
			overrideBiomeCheckOptions.add((Option<Boolean>) namespaceBiomeCheckOptions.get(BiomeCheckOptions.OVERRIDE_GLOBAL_BIOME_CHECK_OPTION_NAME));
			enableBiomeCheckOptions.add((Option<Boolean>) namespaceBiomeCheckOptions.get(BiomeCheckOptions.BIOME_CHECK_IS_ENABLED_OPTION_NAME));
		}

		var invisibleGroup = new InvisibleOptionGroup.Builder().name(Component.literal(""));
		invisibleGroup.option(YACLUtil.createEmptyLabelOption());
		structureCategoryBuilder.group(invisibleGroup.build());
	}

	private static Option<Boolean> addStructure(
		StructureData structureData,
		String structureId,
		HolderLookup.RegistryLookup<Biome> biomeRegistry
	) {
		var structureOptionBuilder = Option.<Boolean>createBuilder()
			.name(LanguageUtil.translateId("structure", structureId))
			.binding(
				true,
				() -> !structureData.isDisabled(),
				isEnabled -> structureData.setDisabled(!isEnabled)
			)
			.controller(opt -> StructureButtonControllerBuilder.create(opt, structureId)
				.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.enabled"):Component.translatable("gui.structurify.label.disabled"))
				.coloured(true));

		structureOptionBuilder.description(v -> {
			var descriptionBuilder = OptionDescription.createBuilder();

			descriptionBuilder.text(Component.translatable("gui.structurify.structures.biomes_description").append(Component.literal("\n")));

			for (String biome : structureData.getBiomes()) {
				if (biome.contains("#")) {
					if (biomeRegistry == null) {
						continue;
					}

					var biomeTagKey = TagKey.create(Registries.BIOME, Structurify.makeNamespacedId(biome.replace("#", "")));
					var biomeTagHolder = biomeRegistry.get(biomeTagKey).orElse(null);

					if (biomeTagHolder == null) {
						continue;
					}

					for (var biomeHolder : biomeTagHolder.stream().toList()) {
						descriptionBuilder.text(Component.literal(" - ").append(LanguageUtil.translateId("biome", biomeHolder.unwrap().left().get().location().toLanguageKey())));
					}
				} else {
					descriptionBuilder.text(Component.literal(" - ").append(LanguageUtil.translateId("biome", biome)));
				}
			}

			descriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structures.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

			return descriptionBuilder.build();
		});

		return structureOptionBuilder.build();
	}
}