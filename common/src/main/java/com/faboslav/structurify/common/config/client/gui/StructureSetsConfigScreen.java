package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.StructurifyClient;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.DualControllerBuilder;
import com.faboslav.structurify.common.config.client.api.controller.builder.StructureButtonControllerBuilder;
import com.faboslav.structurify.common.config.client.api.option.HolderOption;
import com.faboslav.structurify.common.config.client.api.option.InvisibleOptionGroup;
import com.faboslav.structurify.common.config.client.api.option.OptionPair;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.util.LanguageUtil;
import com.faboslav.structurify.common.util.StructurifyComparators;
import com.faboslav.structurify.common.util.TextUtil;
import com.faboslav.structurify.common.util.YACLUtil;
import com.faboslav.structurify.common.versions.VersionedGui;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.*;

public final class StructureSetsConfigScreen
{
	public static Map<String, AbstractMap.SimpleEntry<Option<Boolean>, Option<OptionPair<Option<Integer>, Option<Integer>>>>> structureSetOptions = new HashMap<>();
	public static Option<Boolean> enableGlobalSpacingAndSeparationOption = null;
	public static Option<Double> globalSpacingAndSeparationModifierOption = null;

	public static void createStructureSetsTab(YetAnotherConfigLib.Builder yacl, StructurifyConfig config) {
		var structureSetCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets_category.title"))
			.tooltip(Component.translatable("gui.structurify.structure_sets_category.description").append("\n\n").append(Component.translatable("gui.structurify.structure_sets.spacing.description")).append("\n\n").append(Component.translatable("gui.structurify.structure_sets.separation.description")));

		addGlobalOptions(structureSetCategoryBuilder, config);
		addStructureSets(structureSetCategoryBuilder, config);

		yacl.category(structureSetCategoryBuilder.build());
	}

	private static void addStructureSets(ConfigCategory.Builder structureSetCategoryBuilder, StructurifyConfig config) {
		var structureSets = WorldgenDataProvider.getStructureSets();
		var structureSetGroups = new TreeMap<String, TreeMap<Identifier, StructureSetData>>(StructurifyComparators.ALPHABETICALL_NAMESPACE_COMPARATOR);

		for (Map.Entry<String, StructureSetData> entry : structureSets.entrySet()) {
			String structureSetStringId = entry.getKey();
			Identifier structureSetId = Structurify.makeNamespacedId(structureSetStringId);
			String structureSetNamespace = structureSetId.getNamespace();
			StructureSetData structureSetData = entry.getValue();

			structureSetGroups
				.computeIfAbsent(structureSetNamespace, namespace -> new TreeMap<>(Comparator.comparing(Identifier::getPath)))
				.put(structureSetId, structureSetData);
		}

		for (var structureSetGroup : structureSetGroups.entrySet()) {
			String structureSetNamespace = structureSetGroup.getKey();
			var namespaceStructureSets = structureSetGroup.getValue();

			var invisibleGroup = new InvisibleOptionGroup.Builder().name(Component.literal(""));
			invisibleGroup.option(YACLUtil.createEmptyLabelOption());
			structureSetCategoryBuilder.group(invisibleGroup.build());

			OptionGroup.Builder namespaceGroupBuilder = OptionGroup.createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.structure_group.title", LanguageUtil.translateId(null, structureSetNamespace)).withStyle(style -> style.withUnderlined(true)))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structure_sets.structure_group.description", structureSetNamespace)));

			for (var namespaceStructureSet : namespaceStructureSets.entrySet()) {
				addStructureSet(namespaceGroupBuilder, config, namespaceStructureSet.getKey().toString(), namespaceStructureSet.getValue());
			}

			structureSetCategoryBuilder.group(namespaceGroupBuilder.build());
		}

		var invisibleGroup = new InvisibleOptionGroup.Builder().name(Component.literal(""));
		invisibleGroup.option(YACLUtil.createEmptyLabelOption());
		structureSetCategoryBuilder.group(invisibleGroup.build());
	}

	private static void addStructureSet(
		OptionGroup.Builder currentGroupBuilder,
		StructurifyConfig config,
		String structureSetStringId,
		StructureSetData structureSetData
	) {
		var translatedStructureSetName = LanguageUtil.translateId("structure", structureSetStringId);

		currentGroupBuilder.option(LabelOption.createBuilder().line(translatedStructureSetName.copy().withStyle(style -> style.withBold(true))).build());

		var isEnabledOption = Option.<Boolean>createBuilder()
			.name(translatedStructureSetName)
			.binding(
				!StructureSetData.IS_DISABLED_DEFAULT_VALUE,
				() -> !structureSetData.isDisabled(),
				isEnabled -> structureSetData.setDisabled(!isEnabled)
			)
			.controller(opt -> StructureButtonControllerBuilder.create(opt, structureSetStringId)
				.openConfigCallback((screen, id) -> {
					var configScreen = StructurifyClient.getConfigScreen();

					if (configScreen == null) {
						return;
					}

					screen.finishOrSave();

					YACLScreen structureSetScreen = StructureSetConfigScreen.create(Structurify.getConfig(), id, screen);

					configScreen.saveScreenState(screen);
					VersionedGui.getGui().setScreen(structureSetScreen);
					configScreen.loadScreenState(structureSetScreen);
				})
				.buttonTooltip("gui.structurify.structures.structure_set.config_button.tooltip")
				.formatValue(val -> val ? Component.translatable("gui.structurify.label.enabled"):Component.translatable("gui.structurify.label.disabled"))
				.coloured(true)
			).build();

		currentGroupBuilder.option(isEnabledOption);

		var defaultSalt = structureSetData.getDefaultSalt();

		if (defaultSalt != 0) {
			var saltDescriptionBuilder = OptionDescription.createBuilder();
			saltDescriptionBuilder.text(TextUtil.createTextWithPrefix(translatedStructureSetName, "gui.structurify.structure_sets.salt.description"));

			var saltOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.salt.title"))
				.description(saltDescriptionBuilder.build())
				.binding(
					structureSetData.getDefaultSalt(),
					structureSetData::getSalt,
					structureSetData::setSalt
				)
				.controller(opt -> IntegerFieldControllerBuilder.create(opt).range(StructureSetData.MIN_SALT, StructureSetData.MAX_SALT).formatValue((value) -> Component.literal(value.toString()))).build();

			currentGroupBuilder.option(saltOption);
		}

		var frequencyDescriptionBuilder = OptionDescription.createBuilder();
		frequencyDescriptionBuilder.text(TextUtil.createTextWithPrefix(translatedStructureSetName, "gui.structurify.structure_sets.frequency.description"));

		var defaultFrequency = structureSetData.getDefaultFrequency();

		if (defaultFrequency != 0) {
			var frequencyOption = Option.<Float>createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.frequency.title"))
				.description(frequencyDescriptionBuilder.build())
				.binding(
					structureSetData.getDefaultFrequency(),
					structureSetData::getFrequency,
					structureSetData::setFrequency
				)
				.controller(opt -> FloatSliderControllerBuilder.create(opt).range(StructureSetData.MIN_FREQUENCY, StructureSetData.MAX_FREQUENCY).step(0.001F).formatValue((value) -> Component.literal(String.format(Locale.ROOT, "%.3f", value)))).build();

			currentGroupBuilder.option(frequencyOption);
		}

		var defaultSpacing = structureSetData.getDefaultSpacing();
		var defaultSeparation = structureSetData.getDefaultSeparation();

		if (defaultSpacing != 0 && defaultSeparation != 0) {
			var overrideGlobalSpacingAndSeparationModifierDescriptionBuilder = OptionDescription.createBuilder();
			overrideGlobalSpacingAndSeparationModifierDescriptionBuilder.text(TextUtil.createTextWithPrefix(translatedStructureSetName, Component.translatable("gui.structurify.structure_sets.override_global_spacing_and_separation_modifier.description", translatedStructureSetName)));

			var overrideGlobalSpacingAndSeparationModifierOption = Option.<Boolean>createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.override_global_spacing_and_separation_modifier.title"))
				.description(overrideGlobalSpacingAndSeparationModifierDescriptionBuilder.build())
				.binding(
					structureSetData.getDefaultOverrideGlobalSpacingAndSeparationModifier(),
					structureSetData::overrideGlobalSpacingAndSeparationModifier,
					structureSetData::setOverrideGlobalSpacingAndSeparationModifier
				)
				.controller(opt -> BooleanControllerBuilder.create(opt)
					.formatValue(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
					.coloured(true)
				).available(config.enableGlobalSpacingAndSeparationModifier)
				.build();

			overrideGlobalSpacingAndSeparationModifierOption.addListener((opt, enableGlobalSpacingAndSeparationModifier) -> {
				boolean available = enableGlobalSpacingAndSeparationModifier;

				var structureSetOption = structureSetOptions.get(structureSetStringId);
				structureSetOption.getValue().setAvailable(available);
			});

			var spacingDescriptionBuilder = OptionDescription.createBuilder();
			spacingDescriptionBuilder.text(TextUtil.createTextWithPrefix(translatedStructureSetName, Component.translatable("gui.structurify.structure_sets.spacing.description")));

			var spacingOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.spacing.title"))
				.description(spacingDescriptionBuilder.build())
				.binding(
					structureSetData.getDefaultSpacing(),
					structureSetData::getSpacing,
					structureSetData::setSpacing
				)
				.controller(opt -> IntegerFieldControllerBuilder
					.create(opt)
					.formatValue(spacing -> {
						var structureSetOption = structureSetOptions.get(structureSetStringId);
						var overrideSpacingAndSeparationModifierDescription = structureSetOption.getKey().pendingValue();

						if (enableGlobalSpacingAndSeparationOption.pendingValue() && !overrideSpacingAndSeparationModifierDescription) {
							spacing = (int) (spacing * globalSpacingAndSeparationModifierOption.pendingValue());
						}

						return Component.literal(String.valueOf(spacing));
					})
					.range(0, StructureSetData.MAX_SPACING)
				)
				.available(!config.enableGlobalSpacingAndSeparationModifier || structureSetData.overrideGlobalSpacingAndSeparationModifier())
				.build();

			var separationDescriptionBuilder = OptionDescription.createBuilder();
			separationDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.separation.description"));
			separationDescriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structure_sets.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

			var separationOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.separation.title"))
				.description(separationDescriptionBuilder.build())
				.binding(
					structureSetData.getDefaultSeparation(),
					structureSetData::getSeparation,
					structureSetData::setSeparation
				)
				.controller(opt -> IntegerFieldControllerBuilder
					.create(opt)
					.formatValue(separation -> {
						var structureSetOption = structureSetOptions.get(structureSetStringId);
						var overrideSpacingAndSeparationModifierDescription = structureSetOption.getKey().pendingValue();

						if (enableGlobalSpacingAndSeparationOption.pendingValue() && !overrideSpacingAndSeparationModifierDescription) {
							separation = (int) (separation * globalSpacingAndSeparationModifierOption.pendingValue());
						}

						return Component.literal(String.valueOf(separation));
					})
					.range(0, StructureSetData.MAX_SEPARATION)
				)
				.available(!config.enableGlobalSpacingAndSeparationModifier || structureSetData.overrideGlobalSpacingAndSeparationModifier())
				.build();

			spacingOption.addListener((opt, spacing) -> {
				if (spacing <= separationOption.pendingValue()) {
					spacingOption.requestSet(separationOption.pendingValue() + 1);
				}
			});

			separationOption.addListener((opt, separation) -> {
				if (separation >= spacingOption.pendingValue()) {
					separationOption.requestSet(spacingOption.pendingValue() - 1);
				}
			});

			var spacingAndSeparationPair = new OptionPair<>(spacingOption, separationOption);
			var spacingAndSeparationOption =
				HolderOption.createBuilder(spacingAndSeparationPair)
					.controller(opt -> DualControllerBuilder.create(spacingAndSeparationPair))
					.available(!config.enableGlobalSpacingAndSeparationModifier || structureSetData.overrideGlobalSpacingAndSeparationModifier())
					.build();

			currentGroupBuilder.option(overrideGlobalSpacingAndSeparationModifierOption);
			currentGroupBuilder.option(spacingAndSeparationOption);

			structureSetOptions.put(structureSetStringId, new AbstractMap.SimpleEntry<>(overrideGlobalSpacingAndSeparationModifierOption, spacingAndSeparationOption));
		}

		currentGroupBuilder.option(YACLUtil.createEmptyLabelOption(translatedStructureSetName.copy()));
	}

	public static void addGlobalOptions(ConfigCategory.Builder categoryBuilder, StructurifyConfig config) {
		var generalStructuresSetsGroupBuilder = OptionGroup.createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation.title").withStyle(style -> style.withUnderlined(true)))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation.description")));

		generalStructuresSetsGroupBuilder.option(LabelOption.create(Component.translatable("gui.structurify.structure_sets.structure_spread.title").withStyle(style -> style.withBold(true))));

		var enableGlobalSpacingAndSeparationDescriptionBuilder = OptionDescription.createBuilder();
		enableGlobalSpacingAndSeparationDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.enable_global_spacing_and_separation_modifier.description"));
		enableGlobalSpacingAndSeparationDescriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structure_sets.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

		enableGlobalSpacingAndSeparationOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets.enable_global_spacing_and_separation_modifier.title"))
			.description(enableGlobalSpacingAndSeparationDescriptionBuilder.build())
			.binding(
				StructurifyConfig.ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE,
				() -> config.enableGlobalSpacingAndSeparationModifier,
				enableGlobalSpacingAndSeparationModifier -> config.enableGlobalSpacingAndSeparationModifier = enableGlobalSpacingAndSeparationModifier
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.formatValue(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
				.coloured(true)).build();

		generalStructuresSetsGroupBuilder.option(enableGlobalSpacingAndSeparationOption);

		enableGlobalSpacingAndSeparationOption.addListener((opt, enableGlobalSpacingAndSeparationModifier) -> {
			for (var structureSetOption : structureSetOptions.entrySet()) {
				var structureSetOptionOverride = structureSetOption.getValue().getKey();
				var structureSetOptionPair = structureSetOption.getValue().getValue();

				structureSetOptionOverride.setAvailable(enableGlobalSpacingAndSeparationModifier);
				structureSetOptionPair.setAvailable(!enableGlobalSpacingAndSeparationModifier || structureSetOptionOverride.pendingValue());
			}
		});

		var globalSpacingAndSeparationModifierDescriptionBuilder = OptionDescription.createBuilder();
		globalSpacingAndSeparationModifierDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation_modifier.description"));
		globalSpacingAndSeparationModifierDescriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structure_sets.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

		globalSpacingAndSeparationModifierOption = Option.<Double>createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation_modifier.title"))
			.description(globalSpacingAndSeparationModifierDescriptionBuilder.build())
			.binding(
				StructurifyConfig.GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE,
				() -> config.globalSpacingAndSeparationModifier,
				modifier -> config.globalSpacingAndSeparationModifier = modifier
			)
			.controller(opt -> DoubleFieldControllerBuilder.create(opt).min(0.1D)).build();

		generalStructuresSetsGroupBuilder.option(globalSpacingAndSeparationModifierOption);

		categoryBuilder.group(generalStructuresSetsGroupBuilder.build());
	}
}