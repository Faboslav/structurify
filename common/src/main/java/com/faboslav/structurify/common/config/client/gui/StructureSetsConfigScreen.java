package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.DualControllerBuilder;
import com.faboslav.structurify.common.config.client.api.option.HolderOption;
import com.faboslav.structurify.common.config.client.api.option.OptionPair;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

@Environment(EnvType.CLIENT)
public final class StructureSetsConfigScreen
{
	public static Screen createConfigGui(StructurifyConfig config, Screen parent) {
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());

		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Component.translatable("gui.structurify.title"))
			.save(config::save);

		createStructureSetsTab(yacl, config);

		return yacl.build().generateScreen(parent);
	}

	public static void createStructureSetsTab(YetAnotherConfigLib.Builder yacl, StructurifyConfig config) {
		var structureSetOptions = new HashMap<String, AbstractMap.SimpleEntry<Option<Boolean>, Option<OptionPair<Option<Integer>, Option<Integer>>>>>();

		var structureSetCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets_category.title"))
			.tooltip(Component.translatable("gui.structurify.structure_sets_category.description").append("\n\n").append(Component.translatable("gui.structurify.structure_sets.spacing.description")).append("\n\n").append(Component.translatable("gui.structurify.structure_sets.separation.description")));

		var generalStructuresSetsGroupBuilder = OptionGroup.createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation.description")));

		var enableGlobalSpacingAndSeparationDescriptionBuilder = OptionDescription.createBuilder();
		enableGlobalSpacingAndSeparationDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.enable_global_spacing_and_separation_modifier.description"));
		enableGlobalSpacingAndSeparationDescriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structure_sets.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

		var enableGlobalSpacingAndSeparationOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets.enable_global_spacing_and_separation_modifier.title"))
			.description(enableGlobalSpacingAndSeparationDescriptionBuilder.build())
			.binding(
				StructurifyConfig.ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE,
				() -> config.enableGlobalSpacingAndSeparationModifier,
				enableGlobalSpacingAndSeparationModifier -> config.enableGlobalSpacingAndSeparationModifier = enableGlobalSpacingAndSeparationModifier
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
				.coloured(true)).build();

		generalStructuresSetsGroupBuilder.option(enableGlobalSpacingAndSeparationOption);

		enableGlobalSpacingAndSeparationOption.addListener((opt, enableGlobalSpacingAndSeparationModifier) -> {
			for(var structureSetOption : structureSetOptions.values()) {
				structureSetOption.getValue().setAvailable(!enableGlobalSpacingAndSeparationModifier || structureSetOption.getKey().pendingValue());
			}
		});

		var globalSpacingAndSeparationModifierDescriptionBuilder = OptionDescription.createBuilder();
		globalSpacingAndSeparationModifierDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation_modifier.description"));
		globalSpacingAndSeparationModifierDescriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structure_sets.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

		var globalSpacingAndSeparationModifierOption = Option.<Double>createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation_modifier.title"))
			.description(globalSpacingAndSeparationModifierDescriptionBuilder.build())
			.binding(
				StructurifyConfig.GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE,
				() -> config.globalSpacingAndSeparationModifier,
				modifier -> config.globalSpacingAndSeparationModifier = modifier
			)
			.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.1D, 100.0D).step(0.1D)).build();

		generalStructuresSetsGroupBuilder.option(globalSpacingAndSeparationModifierOption);

		structureSetCategoryBuilder.group(generalStructuresSetsGroupBuilder.build());

		var structureSets = WorldgenDataProvider.getStructureSets();

		List<OptionGroup> optionGroups = new ArrayList<>();
		OptionGroup.Builder currentGroupBuilder = null;
		String currentNamespace = null;


		for (Map.Entry<String, StructureSetData> entry : structureSets.entrySet()) {
			String structureSetStringId = entry.getKey();
			ResourceLocation structureSetId = Structurify.makeNamespacedId(structureSetStringId);
			String namespace = structureSetId.getNamespace();

			// Create new group for each namespace
			if (!namespace.equals(currentNamespace)) {
				if (currentGroupBuilder != null) {
					optionGroups.add(currentGroupBuilder.build());
				}

				// Create new group
				currentGroupBuilder = OptionGroup.createBuilder()
					.name(Component.translatable("gui.structurify.structure_sets.structure_group.title", LanguageUtil.translateId(null, namespace)))
					.description(OptionDescription.of(Component.translatable("gui.structurify.structure_sets.structure_group.description", namespace)));
				currentNamespace = namespace;
			}

			var translatedStructureSetName = LanguageUtil.translateId("structure", structureSetStringId);

			var overrideGlobalSpacingAndSeparationModifierDescriptionBuilder = OptionDescription.createBuilder();
			overrideGlobalSpacingAndSeparationModifierDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.override_global_spacing_and_separation_modifier.description"));

			var overrideGlobalSpacingAndSeparationModifierOption = Option.<Boolean>createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.override_global_spacing_and_separation_modifier.title"))
				.description(overrideGlobalSpacingAndSeparationModifierDescriptionBuilder.build())
				.binding(
					StructureSetData.OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE,
					() -> config.getStructureSetData().get(structureSetStringId).overrideGlobalSpacingAndSeparationModifier(),
					overrideGlobalSpacingAndSeparationModifier -> config.getStructureSetData().get(structureSetStringId).setOverrideGlobalSpacingAndSeparationModifier(overrideGlobalSpacingAndSeparationModifier)
				)
				.controller(opt -> BooleanControllerBuilder.create(opt)
					.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
					.coloured(true)).build();

			overrideGlobalSpacingAndSeparationModifierOption.addListener((opt, enableGlobalSpacingAndSeparationModifier) -> {
				var structureSetOption = structureSetOptions.get(structureSetStringId);
				structureSetOption.getValue().setAvailable(!enableGlobalSpacingAndSeparationOption.pendingValue() || structureSetOption.getKey().pendingValue());
			});

			var spacingDescriptionBuilder = OptionDescription.createBuilder();
			spacingDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.spacing.description"));

			var spacingOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.spacing.title"))
				.description(spacingDescriptionBuilder.build())
				.binding(
					config.getStructureSetData().get(structureSetStringId).getDefaultSpacing(),
					() -> config.getStructureSetData().get(structureSetStringId).getSpacing(),
					spacing -> config.getStructureSetData().get(structureSetStringId).setSpacing(spacing)
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, StructureSetData.MAX_SPACING).step(1)).build();

			var separationDescriptionBuilder = OptionDescription.createBuilder();
			separationDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.separation.description"));
			separationDescriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structure_sets.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

			var separationOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.separation.title"))
				.description(separationDescriptionBuilder.build())
				.binding(
					config.getStructureSetData().get(structureSetStringId).getDefaultSeparation(),
					() -> config.getStructureSetData().get(structureSetStringId).getSeparation(),
					separation -> config.getStructureSetData().get(structureSetStringId).setSeparation(separation)
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, StructureSetData.MAX_SEPARATION).step(1)).build();

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
			var spacingAndSeparationOption = HolderOption.<Option<Integer>, Option<Integer>>createBuilder()
				.controller(opt -> DualControllerBuilder.create(LabelOption.createBuilder().line(translatedStructureSetName).build(), spacingOption, separationOption))
				.available(!config.enableGlobalSpacingAndSeparationModifier || config.getStructureSetData().get(structureSetStringId).overrideGlobalSpacingAndSeparationModifier()).build();

			currentGroupBuilder.option(LabelOption.createBuilder().line(translatedStructureSetName).build());
			currentGroupBuilder.option(overrideGlobalSpacingAndSeparationModifierOption);
			currentGroupBuilder.option(spacingAndSeparationOption);

			structureSetOptions.put(structureSetStringId, new AbstractMap.SimpleEntry<>(overrideGlobalSpacingAndSeparationModifierOption, spacingAndSeparationOption));
		}

		if (currentGroupBuilder != null) {
			optionGroups.add(currentGroupBuilder.build());
		}

		for (OptionGroup structureOptionGroup : optionGroups) {
			structureSetCategoryBuilder.group(structureOptionGroup);
		}

		yacl.category(structureSetCategoryBuilder.build());
	}
}