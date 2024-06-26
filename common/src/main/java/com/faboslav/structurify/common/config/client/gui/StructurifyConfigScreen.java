package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.DualControllerBuilder;
import com.faboslav.structurify.common.config.client.api.controller.builder.StructureButtonControllerBuilder;
import com.faboslav.structurify.common.config.client.api.option.HolderOption;
import com.faboslav.structurify.common.config.data.StructureData;
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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class StructurifyConfigScreen
{
	public static Screen createConfigGui(StructurifyConfig config, Screen parent) {
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());

		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Text.translatable("gui.structurify.title"))
			.save(config::save);

		createStructuresTab(yacl, config);
		createStructureSetsTab(yacl, config);

		return yacl.build().generateScreen(parent);
	}

	public static void createStructuresTab(YetAnotherConfigLib.Builder yacl, StructurifyConfig config) {
		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Text.translatable("gui.structurify.structures_category.title"))
			.tooltip(Text.translatable("gui.structurify.structures_category.description"));

		var generalStructuresGroupBuilder = OptionGroup.createBuilder()
			.name(Text.translatable("gui.structurify.structures.general.title"))
			.description(OptionDescription.of(Text.literal("gui.structurify.structures.general.description")));

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

				// Create new group
				currentGroupBuilder = OptionGroup.createBuilder()
					.name(Text.literal(LanguageUtil.translateId(null, namespace).getString()).append(" ").append(Text.translatable("gui.structurify.structures.structures_group.title")))
					.description(OptionDescription.of(Text.translatable("gui.structurify.structures.structures_group.description" + namespace)));

				currentNamespace = namespace;
			}

			var optionBuilder = Option.<Boolean>createBuilder()
				.name(LanguageUtil.translateId("structure", structureStringId))
				.binding(
					true,
					() -> !config.getStructureData().get(structureStringId).isDisabled(),
					isEnabled -> config.getStructureData().get(structureStringId).setDisabled(!isEnabled)
				)
				.controller(opt -> StructureButtonControllerBuilder.create(opt, structureStringId)
					.valueFormatter(val -> val ? Text.literal("Enabled"):Text.literal("Disabled"))
					.coloured(true));

			var descriptionBuilder = OptionDescription.createBuilder();

			descriptionBuilder.text(Text.translatable("structurized.structures.biomes_description").append(Text.literal("\n")));

			for (String biome : structureData.getBiomes()) {
				descriptionBuilder.text(Text.literal(" - ").append(LanguageUtil.translateId("biome", biome)));
			}

			descriptionBuilder.text(Text.literal("\n\n").append(Text.translatable("structurized.structures.warning")).styled(style -> style.withColor(Formatting.YELLOW)));

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
			.name(Text.translatable("gui.structurify.structure_sets.title"))
			.tooltip(Text.translatable("gui.structurify.structure_sets.description").append("\n\n").append(Text.translatable("gui.structurify.structure_sets.spacing.description")).append("\n\n").append(Text.translatable("gui.structurify.structure_sets.separation.description")));

		var generalStructuresSetsGroupBuilder = OptionGroup.createBuilder()
			.name(Text.translatable("gui.structurify.structure_sets.global_spacing_and_separation.title"))
			.description(OptionDescription.of(Text.translatable("gui.structurify.structure_sets.global_spacing_and_separation.description")));

		var enableGlobalSpacingAndSeparationBuilder = Option.<Boolean>createBuilder()
			.name(Text.translatable("gui.structurify.structure_sets.enable_global_spacing_and_separation_modifier.title"))
			.description(OptionDescription.of(Text.translatable("gui.structurify.structure_sets.enable_global_spacing_and_separation_modifier.description")))
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
			.name(Text.translatable("gui.structurify.structure_sets.global_spacing_and_separation_modifier.title"))
			.description(OptionDescription.of(Text.translatable("gui.structurify.structure_sets.global_spacing_and_separation_modifier.description")))
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

				// Create new group
				currentGroupBuilder = OptionGroup.createBuilder()
					.name(LanguageUtil.translateId(null, namespace).append(" ").append(Text.translatable("gui.structurify.structure_sets.structure_group.title")))
					.description(OptionDescription.of(Text.translatable("gui.structurify.structure_sets.structure_group.description" + LanguageUtil.translateId(null, namespace))));
				currentNamespace = namespace;
			}

			var spacingOption = Option.<Integer>createBuilder()
				.name(Text.translatable("gui.structurify.structure_sets.spacing.title"))
				.description(OptionDescription.of(Text.translatable("gui.structurify.structure_sets.spacing.description")))
				.binding(
					config.getStructureSetData().get(structureSetStringId).getDefaultSpacing(),
					() -> config.getStructureSetData().get(structureSetStringId).getSpacing(),
					spacing -> config.getStructureSetData().get(structureSetStringId).setSpacing(spacing)
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, StructureSetData.MAX_SPACING).step(1)).build();

			var separationOption = Option.<Integer>createBuilder()
				.name(Text.translatable("gui.structurify.structure_sets.separation.title"))
				.description(OptionDescription.of(Text.translatable("gui.structurify.structure_sets.separation.description")))
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
			var spacingAndSeparationOptionBuilder = HolderOption.<Option<Integer>, Option<Integer>>createBuilder()
				.controller(opt -> DualControllerBuilder.create(LabelOption.createBuilder().line(LanguageUtil.translateId("structure", structureSetStringId)).build(), spacingOption, separationOption));

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