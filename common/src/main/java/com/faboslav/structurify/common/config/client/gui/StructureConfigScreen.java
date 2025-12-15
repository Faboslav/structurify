package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.BiomeStringControllerBuilder;
import com.faboslav.structurify.common.config.client.api.option.InvisibleOptionGroup;
import com.faboslav.structurify.common.config.client.gui.structure.*;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class StructureConfigScreen
{
	public static YACLScreen create(StructurifyConfig config, String structureId, Screen parent) {
		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Component.literal(structureId))
			.save(config::save);

		var structureData = config.getStructureData().get(structureId);
		var translatedStructureName = LanguageUtil.translateId("structure", structureId);
		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.title", translatedStructureName))
			.tooltip(Component.translatable("gui.structurify.structures.structure.description", translatedStructureName));

		var structureSettingsGroup = new InvisibleOptionGroup.Builder().name(Component.literal("test"));

		structureSettingsGroup.option(LabelOption.create(Component.translatable("gui.structurify.structures.structures.structure.title").withStyle(style -> style.withBold(true))));

		var isDisabledOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.is_disabled.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.is_disabled.description")))
			.binding(
				StructureData.IS_DISABLED_DEFAULT_VALUE,
				() -> config.getStructureData().get(structureId).isDisabled(),
				isDisabled -> config.getStructureData().get(structureId).setDisabled(isDisabled)
			)
			.controller(opt -> BooleanControllerBuilder.create(opt).formatValue(val -> val ? Component.translatable("gui.structurify.label.yes").withStyle(style -> style.withColor(ChatFormatting.RED)):Component.translatable("gui.structurify.label.no").withStyle(style -> style.withColor(ChatFormatting.GREEN)))).build();

		structureSettingsGroup.option(isDisabledOption);

		var stepOption = Option.<GenerationStep.Decoration>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.step.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.step.description")))
			.binding(
				config.getStructureData().get(structureId).getDefaultStep(),
				() -> config.getStructureData().get(structureId).getStep(),
				step -> config.getStructureData().get(structureId).setStep(step)
			).controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(GenerationStep.Decoration.class)
				.formatValue(step -> Component.translatable(getHumanReadableName(step.name().toLowerCase())))).build();

		structureSettingsGroup.option(stepOption);

		var terrainAdaptationOption = Option.<TerrainAdjustment>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.terrain_adaptation.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.terrain_adaptation.description")))
			.binding(
				config.getStructureData().get(structureId).getDefaultTerrainAdaptation(),
				() -> config.getStructureData().get(structureId).getTerrainAdaptation(),
				terrainAdaptation -> config.getStructureData().get(structureId).setTerrainAdaptation(terrainAdaptation)
			).controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(TerrainAdjustment.class)
				.formatValue(terrainAdaptation -> Component.translatable(getHumanReadableName(terrainAdaptation.name().toLowerCase())))).build();

		structureSettingsGroup.option(terrainAdaptationOption);

		if (config.getStructureData().get(structureId).isJigsawStructure()) {
			var jigsawOptions = JigsawOptions.addJigsawOptions(structureData);
			structureSettingsGroup.options(jigsawOptions);

			isDisabledOption.addListener((opt, currentIsDisabled) -> {
				for (var jigsawOption : jigsawOptions) {
					jigsawOption.setAvailable(!currentIsDisabled);
				}
			});
		}

		structureCategoryBuilder.group(structureSettingsGroup.build());

		var biomesOption = ListOption.<String>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.biomes.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.biomes.description", translatedStructureName)))
			.insertEntriesAtEnd(false)
			.binding(
				config.getStructureData().get(structureId).getDefaultBiomes(),
				() -> config.getStructureData().get(structureId).getBiomes(),
				biomes -> config.getStructureData().get(structureId).setBiomes(biomes)
			)
			.controller(BiomeStringControllerBuilder::create)
			.initial("").build();

		structureCategoryBuilder.group(biomesOption);

		var distanceFromWorldCenterOptionsGroup = new InvisibleOptionGroup.Builder().name(Component.literal("distance"));
		DistanceFromWorldCenterOptions.addDistanceFromWorldCenterOptions(distanceFromWorldCenterOptionsGroup, config, structureId);
		structureCategoryBuilder.group(distanceFromWorldCenterOptionsGroup.build());

		var overlapOptionsGroup = new InvisibleOptionGroup.Builder().name(Component.literal("overlap"));
		OverlapCheckOptions.addOverlapCheckOptions(overlapOptionsGroup, config, structureId);
		structureCategoryBuilder.group(overlapOptionsGroup.build());

		var flatnessOptionsGroup = new InvisibleOptionGroup.Builder().name(Component.literal("flatness"));
		FlatnessCheckOptions.addFlatnessCheckOptions(flatnessOptionsGroup, config, structureId);
		structureCategoryBuilder.group(flatnessOptionsGroup.build());

		var biomesOptionsGroup = new InvisibleOptionGroup.Builder().name(Component.literal("biomes"));
		var biomeCheckOptions = BiomeCheckOptions.addBiomeCheckOptions(biomesOptionsGroup, config, structureId);
		structureCategoryBuilder.group(biomesOptionsGroup.build());
		var blacklistedBiomesOption = biomeCheckOptions.get(BiomeCheckOptions.BIOME_CHECK_BLACKLISTED_BIOMES_OPTION_NAME);
		structureCategoryBuilder.group((OptionGroup) blacklistedBiomesOption);

		/*
		isDisabledOption.addListener((opt, currentIsDisabled) -> {
			for(var biomeCheckOption : biomeCheckOptions) {
				biomeCheckOption.setAvailable(!currentIsDisabled);
			}
		});*/

		yacl.category(structureCategoryBuilder.build());

		return (YACLScreen) yacl.build().generateScreen(parent);
	}

	private static String getHumanReadableName(String serializedName) {
		return Arrays.stream(serializedName.split("_"))
			.map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
			.collect(Collectors.joining(" "));
	}
}