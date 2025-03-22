package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.client.api.controller.builder.BiomeStringControllerBuilder;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public final class StructureConfigScreen
{
	public static YACLScreen create(Screen parent, String structureId) {
		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Component.literal(structureId))
			.save(Structurify.getConfig()::save);

		var translatedStructureName = LanguageUtil.translateId("structure", structureId);
		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.title", translatedStructureName))
			.tooltip(Component.translatable("gui.structurify.structures.structure.description"));

		structureCategoryBuilder.option(LabelOption.create(Component.translatable("gui.structurify.structures.flatness_check_group.title")));

		var enableFlatnessCheckOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.enable_flatness_check.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.enable_flatness_check.description")))
			.binding(
				StructureData.ENABLE_FLATNESS_CHECK_DEFAULT_VALUE,
				() -> Structurify.getConfig().getStructureData().get(structureId).isFlatnessCheckEnabled(),
				enableFlatnessCheck -> Structurify.getConfig().getStructureData().get(structureId).setEnableFlatnessCheck(enableFlatnessCheck)
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
				.coloured(true)).build();

		structureCategoryBuilder.option(enableFlatnessCheckOption);

		var flatnessCheckDistanceOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.flatness_check_distance.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.flatness_check_distance.description")))
			.binding(
				Structurify.getConfig().getStructureData().get(structureId).getDefaultCheckDistance(),
				() -> Structurify.getConfig().getStructureData().get(structureId).getFlatnessCheckDistance(),
				flatnessCheckDistance -> Structurify.getConfig().getStructureData().get(structureId).setFlatnessCheckDistance(flatnessCheckDistance)
			)
			.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 256).step(1)).build();

		structureCategoryBuilder.option(flatnessCheckDistanceOption);

		var flatnessCheckThresholdOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.flatness_check_threshold.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.flatness_check_threshold.description")))
			.binding(
				StructureData.FLATNESS_CHECK_THRESHOLD_DEFAULT_VALUE,
				() -> Structurify.getConfig().getStructureData().get(structureId).getFlatnessCheckThreshold(),
				flatnessCheckThreshold -> Structurify.getConfig().getStructureData().get(structureId).setFlatnessCheckThreshold(flatnessCheckThreshold)
			)
			.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 256).step(1)).build();

		structureCategoryBuilder.option(flatnessCheckThresholdOption);

		structureCategoryBuilder.option(LabelOption.create(Component.translatable("gui.structurify.structures.biome_check_group.title")));

		var enableBiomeCheckOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.enable_biome_check.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.enable_biome_check.description")))
			.binding(
				StructureData.ENABLE_BIOME_CHECK_DEFAULT_VALUE,
				() -> Structurify.getConfig().getStructureData().get(structureId).isBiomeCheckEnabled(),
				enableBiomeCheck -> Structurify.getConfig().getStructureData().get(structureId).setEnableBiomeCheck(enableBiomeCheck)
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
				.coloured(true)).build();

		structureCategoryBuilder.option(enableBiomeCheckOption);

		var biomeCheckDistanceOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.biome_check_distance.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.biome_check_distance.description")))
			.binding(
				Structurify.getConfig().getStructureData().get(structureId).getDefaultCheckDistance(),
				() -> Structurify.getConfig().getStructureData().get(structureId).getBiomeCheckDistance(),
				biomeCheckDistance -> Structurify.getConfig().getStructureData().get(structureId).setBiomeCheckDistance(biomeCheckDistance)
			)
			.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 256).step(1)).build();

		structureCategoryBuilder.option(biomeCheckDistanceOption);

		var biomesOption = ListOption.<String>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.biomes.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.biomes.description", translatedStructureName)))
			.insertEntriesAtEnd(false)
			.binding(
				Structurify.getConfig().getStructureData().get(structureId).getDefaultBiomes(),
				() -> Structurify.getConfig().getStructureData().get(structureId).getBiomes(),
				biomes -> Structurify.getConfig().getStructureData().get(structureId).setBiomes(biomes)
			)
			.controller(BiomeStringControllerBuilder::create)
			.initial("").build();

		structureCategoryBuilder.group(biomesOption);

		yacl.category(structureCategoryBuilder.build());

		return (YACLScreen) yacl.build().generateScreen(parent);
	}
}