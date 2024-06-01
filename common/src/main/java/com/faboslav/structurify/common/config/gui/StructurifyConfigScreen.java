package com.faboslav.structurify.common.config.gui;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.api.controller.BooleanWithButtonControllerBuilder;
import com.faboslav.structurify.common.config.api.controller.DualControllerBuilder;
import com.faboslav.structurify.common.config.api.controller.HolderOption;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.config.util.StructureSetUtil;
import com.faboslav.structurify.common.events.client.ClientLoadedEvent;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class StructurifyConfigScreen
{
	public static Screen createConfigGui(StructurifyConfig config, Screen parent) {
		ClientLoadedEvent.EVENT.invoke(new ClientLoadedEvent());

		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Text.translatable("structurized.name"))
			.save(config::save);

		createStructuresTab(yacl, config);
		createStructureSetsTab(yacl, config);

		return yacl.build().generateScreen(parent);
	}

	public static void createStructuresTab(YetAnotherConfigLib.Builder yacl, StructurifyConfig config) {
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
			.controller(opt -> BooleanControllerBuilder.create(opt).valueFormatter(val -> val ? Text.translatable("Yes").styled(style -> style.withColor(Formatting.RED)):Text.translatable("No").styled(style -> style.withColor(Formatting.GREEN))));

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

				if (!Language.getInstance().hasTranslation(possibleModNameTranslationKey)) {
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

	public static void createStructureSetsTab(YetAnotherConfigLib.Builder yacl, StructurifyConfig config) {
		var structureSetCategoryBuilder = ConfigCategory.createBuilder()
			.name(Text.translatable("gui.structurized.structure_sets.title"))
			.tooltip(Text.translatable("gui.structurized.structure_sets.description"));

		var generalStructuresSetsGroupBuilder = OptionGroup.createBuilder()
			.name(Text.translatable("gui.structurized.structure_sets.global_spacing_and_separation.title"))
			.description(OptionDescription.of(Text.translatable("gui.structurized.structure_sets.global_spacing_and_separation.description")));

		var enableGlobalSpacingAndSeparationBuilder = Option.<Boolean>createBuilder()
			.name(Text.translatable("gui.structurized.structure_sets.enable_global_spacing_and_separation_modifier.title"))
			.description(OptionDescription.of(Text.translatable("gui.structurized.structure_sets.enable_global_spacing_and_separation_modifier.description")))
			.binding(
				false,
				() -> config.enableGlobalSpacingAndSeparationModifier,
				enableGlobalSpacingAndSeparationModifier -> config.enableGlobalSpacingAndSeparationModifier = enableGlobalSpacingAndSeparationModifier
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Text.translatable("Yes"):Text.translatable("No"))
				.coloured(true));

		generalStructuresSetsGroupBuilder.option(enableGlobalSpacingAndSeparationBuilder.build());

		var globalSpacingModifierBuilder = Option.<Double>createBuilder()
			.name(Text.translatable("gui.structurized.structure_sets.global_spacing_and_separation_modifier.title"))
			.description(OptionDescription.of(Text.translatable("gui.structurized.structure_sets.global_spacing_and_separation_modifier.description")))
			.binding(
				1.0D,
				() -> config.globalSpacingAndSeparationModifier,
				modifier -> config.globalSpacingAndSeparationModifier = modifier
			)
			.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.0D, 100.0D).step(0.1D));

		generalStructuresSetsGroupBuilder.option(globalSpacingModifierBuilder.build());
		structureSetCategoryBuilder.group(generalStructuresSetsGroupBuilder.build());

		var structureSets = WorldgenDataProvider.getStructureSets();

		List<OptionGroup> optionGroups = new ArrayList<>();
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

				if (!Language.getInstance().hasTranslation(possibleModNameTranslationKey)) {
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
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, StructureSetData.MAX_SPACING).step(1)).build();

			var separationOption = Option.<Integer>createBuilder()
				.name(Text.translatable("gui.structurized.structure_sets.separation.title"))
				.description(OptionDescription.of(Text.translatable("gui.structurized.structure_sets.separation.description")))
				.binding(
					config.getStructureSetData().get(structureSetStringId).getDefaultSeparation(),
					() -> config.getStructureSetData().get(structureSetStringId).getSeparation(),
					separation -> config.getStructureSetData().get(structureSetStringId).setSeparation(separation)
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, StructureSetData.MAX_SEPARATION).step(1)).build();


			var spacingAndSeparationOptionBuilder = HolderOption.<Option<Integer>, Option<Integer>>createBuilder()
				.controller(opt -> DualControllerBuilder.create(LabelOption.createBuilder().line(Text.literal(structureSetStringId)).build(), spacingOption, separationOption));

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