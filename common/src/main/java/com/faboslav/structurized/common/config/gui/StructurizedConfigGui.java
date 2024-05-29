package com.faboslav.structurized.common.config.gui;

import com.faboslav.structurized.common.Structurized;
import com.faboslav.structurized.common.config.StructurizedConfig;
import com.faboslav.structurized.common.config.api.controller.BooleanWithButtonControllerBuilder;
import com.faboslav.structurized.common.config.api.controller.HolderOption;
import com.faboslav.structurized.common.config.api.controller.OptionPair;
import com.faboslav.structurized.common.config.api.controller.DualControllerBuilder;
import com.faboslav.structurized.common.config.data.StructureData;
import com.faboslav.structurized.common.config.data.StructureSetData;
import com.faboslav.structurized.common.config.data.WorldgenDataProvider;
import com.faboslav.structurized.common.config.util.StructureSetUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.autogen.Label;
import dev.isxander.yacl3.gui.controllers.LabelController;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.structure.StructureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.util.*;

public final class StructurizedConfigGui
{
	public static Screen createConfigGui(StructurizedConfig config, Screen parent) {
		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Text.translatable("structurized.name"))
			.save(config::save);

		createStructuresTab(yacl, config);
		createStructureSpacingTab(yacl, config);

		return yacl.build().generateScreen(parent);
	}

	public static void createStructuresTab(YetAnotherConfigLib.Builder yacl, StructurizedConfig config) {
		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Text.literal("Structures"))
			.tooltip(Text.literal("Choose which structures will be disabled"));

		var generalStructuresGroupBuilder = OptionGroup.createBuilder()
			.name(Text.translatable("gui.structurized.structures.general.title"))
			.description(OptionDescription.of(Text.literal("gui.structurized.structures.general.description")));

		var disableAllStructuresOptionBuilder = Option.<Boolean>createBuilder()
			.name(Text.translatable("structurized.structures.disable_all_structures.title"))
			.description(OptionDescription.of(Text.translatable("structurized.structures.disable_all_structures.description")))
			.binding(
				false,
				() -> config.disableAllStructures,
				disableAllStructures -> config.disableAllStructures = disableAllStructures
			)
			.controller(opt -> BooleanControllerBuilder.create(opt).valueFormatter(val -> val ? Text.translatable("Yes").styled(style -> style.withColor(Formatting.RED)) : Text.translatable("No").styled(style -> style.withColor(Formatting.GREEN))));

		generalStructuresGroupBuilder.option(disableAllStructuresOptionBuilder.build());
		structureCategoryBuilder.group(generalStructuresGroupBuilder.build());

		var structures = WorldgenDataProvider.getStructures();
		List<OptionGroup> optionGroups = new ArrayList<>();
		OptionGroup.Builder currentGroupBuilder = null;
		String currentNamespace = null;

		for (Map.Entry<String, StructureData> entry : structures.entrySet()) {
			String structureStringId = entry.getKey();
			StructureData structureData = entry.getValue();

			Identifier structureId = new Identifier(structureStringId);
			String namespace = structureId.getNamespace();

			// Create new group for each namespace
			if (!namespace.equals(currentNamespace)) {
				if (currentGroupBuilder != null) {
					optionGroups.add(currentGroupBuilder.build());
				}

				String possibleModNameTranslationKey = namespace + "." + namespace;

				if(!Language.getInstance().hasTranslation(possibleModNameTranslationKey)) {
					possibleModNameTranslationKey = namespace;
				}

				// Create new group
				currentGroupBuilder = OptionGroup.createBuilder()
					.name(Text.translatable(possibleModNameTranslationKey).append(" ").append(Text.translatable("gui.structurized.structures")))
					.description(OptionDescription.of(Text.literal("Options for namespace: " + namespace)));
				currentNamespace = namespace;
			}

			var optionBuilder = Option.<Boolean>createBuilder()
				.name(Text.literal(structureStringId))
				.binding(
					true,
					() -> !config.getStructureData().get(structureStringId).isDisabled(),
					isEnabled -> config.getStructureData().get(structureStringId).setDisabled(!isEnabled)
				)
				.controller(opt -> BooleanWithButtonControllerBuilder.create(opt)
					.valueFormatter(val -> val ? Text.literal("Enabled"):Text.literal("Disabled"))
					.coloured(true));

			var descriptionBuilder = OptionDescription.createBuilder();
			descriptionBuilder.text(Text.literal("\n"));
			descriptionBuilder.text(Text.translatable("structurized.structures.biomes_description").styled(style -> style.withColor(Formatting.WHITE)));

			for (String biome : structureData.getBiomes()) {
				descriptionBuilder.text(Text.literal(" • " + biome).styled(style -> style.withColor(Formatting.WHITE)));
			}

			descriptionBuilder.text(Text.literal("\n"));
			descriptionBuilder.text(Text.translatable("structurized.structures.warning").styled(style -> style.withColor(Formatting.YELLOW)));

			optionBuilder.description(descriptionBuilder.build());
			currentGroupBuilder.option(optionBuilder.build());
		}

		if (currentGroupBuilder != null) {
			optionGroups.add(currentGroupBuilder.build());
		}

		for (OptionGroup structureOptionGroup : optionGroups) {
			structureCategoryBuilder.group(structureOptionGroup);
		}

		yacl.category(structureCategoryBuilder.build());
	}

	public static void createStructureSpacingTab(YetAnotherConfigLib.Builder yacl, StructurizedConfig config) {
		var structureSetCategoryBuilder = ConfigCategory.createBuilder()
			.name(Text.literal("gui.structurized.structure_spread.title"))
			.tooltip(Text.literal("gui.structurized.structure_spread.description"));

		var structuresGroupBuilder = OptionGroup.createBuilder()
			.name(Text.translatable("structurized.structure_spacing.title"))
			.description(OptionDescription.of(Text.translatable("structurized.structure_spacing.description")));

		var enableGlobalSpacingAndSeparationBuilder = Option.<Boolean>createBuilder()
			.name(Text.translatable("structurized.structure_spacing.enable_global_spacing_and_separation.title"))
			.description(OptionDescription.of(Text.translatable("structurized.structure_spacing.enable_global_spacing_and_separation.description")))
			.binding(
				true,
				() -> config.enableGlobalSpacingAndSeparation,
				enableGlobalSpacingAndSeparationModifier -> config.enableGlobalSpacingAndSeparation = enableGlobalSpacingAndSeparationModifier
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Text.translatable("Yes"):Text.translatable("No"))
				.coloured(true));

		structuresGroupBuilder.option(enableGlobalSpacingAndSeparationBuilder.build());

		var globalSpacingModifierBuilder = Option.<Double>createBuilder()
			.name(Text.translatable("structurized.structure_spacing.global_spacing_modifier.title"))
			.description(OptionDescription.of(Text.translatable("structurized.structure_spacing.global_spacing_modifier.description")))
			.binding(
				1.0D,
				() -> config.globalSpacingModifier,
				updatedGlobalSpacingModifier -> config.globalSpacingModifier = updatedGlobalSpacingModifier
			)
			.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.0D, 100.0D).step(0.1D));

		structuresGroupBuilder.option(globalSpacingModifierBuilder.build());

		var globalSeparationModifierBuilder = Option.<Double>createBuilder()
			.name(Text.translatable("structurized.structure_spacing.global_separation_modifier.title"))
			.description(OptionDescription.of(Text.translatable("structurized.structure_spacing.global_separation_modifier.description")))
			.binding(
				1.0D,
				() -> config.globalSeparationModifier,
				updatedGlobalSeparationModifier -> config.globalSeparationModifier = updatedGlobalSeparationModifier
			)
			.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.0D, 100.0D).step(0.1D));

		structuresGroupBuilder.option(globalSeparationModifierBuilder.build());

		var structureSets = WorldgenDataProvider.getStructureSets();

		List < OptionGroup > optionGroups = new ArrayList<>();
		OptionGroup.Builder currentGroupBuilder = null;
		String currentNamespace = null;


		for (Map.Entry<String, StructureSetData> entry : structureSets.entrySet()) {
			String structureSetStringId = entry.getKey();

			Identifier structureSetId = new Identifier(structureSetStringId);
			String namespace = structureSetId.getNamespace();

			// Create new group for each namespace
			if (!namespace.equals(currentNamespace)) {
				if (currentGroupBuilder != null) {
					optionGroups.add(currentGroupBuilder.build());
				}

				String possibleModNameTranslationKey = namespace + "." + namespace;

				if(!Language.getInstance().hasTranslation(possibleModNameTranslationKey)) {
					possibleModNameTranslationKey = namespace;
				}

				// Create new group
				currentGroupBuilder = OptionGroup.createBuilder()
					.name(Text.translatable(possibleModNameTranslationKey).append(" ").append(Text.translatable("gui.structurized.structures")))
					.description(OptionDescription.of(Text.literal("Options for namespace: " + namespace)));
				currentNamespace = namespace;
			}

			var spacingOption = Option.<Integer>createBuilder()
				.name(Text.translatable("gui.structurized.structure_sets.spacing.title"))
				.description(OptionDescription.of(Text.translatable("gui.structurized.structure_sets.spacing.description")))
				.binding(
					config.getStructureSetData().get(structureSetStringId).getDefaultSpacing(),
					() -> config.getStructureSetData().get(structureSetStringId).getSpacing(),
					spacing -> config.getStructureSetData().get(structureSetStringId).setSpacing(StructureSetUtil.correctSpacing(spacing, config.getStructureSetData().get(structureSetStringId).getSeparation()))
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 4096).step(1)).build();

			var separationOption = Option.<Integer>createBuilder()
				.name(Text.translatable("gui.structurized.structure_sets.separation.title"))
				.description(OptionDescription.of(Text.translatable("gui.structurized.structure_sets.separation.description")))
				.binding(
					config.getStructureSetData().get(structureSetStringId).getDefaultSeparation(),
					() -> config.getStructureSetData().get(structureSetStringId).getSeparation(),
					separation -> config.getStructureSetData().get(structureSetStringId).setSeparation(separation)
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 4096).step(1)).build();


			var spacingAndSeparationOptionBuilder = HolderOption.<Option<Integer>, Option<Integer>>createBuilder()
				.controller(opt -> DualControllerBuilder.create(LabelOption.createBuilder().line(Text.literal(structureSetStringId)).build(), spacingOption, separationOption));

			// Empty label
			currentGroupBuilder.option(spacingAndSeparationOptionBuilder.build());
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