package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.BiomeStringControllerBuilder;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class StructureConfigScreen
{
	public static YACLScreen create(StructurifyConfig config, String structureId, Screen parent) {
		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Component.literal(structureId))
			.save(config::save);

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
				() -> config.getStructureData().get(structureId).isFlatnessCheckEnabled(),
				enableFlatnessCheck -> config.getStructureData().get(structureId).setEnableFlatnessCheck(enableFlatnessCheck)
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
				.coloured(true)).build();

		structureCategoryBuilder.option(enableFlatnessCheckOption);

		var flatnessCheckDistanceOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.flatness_check_distance.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.flatness_check_distance.description")))
			.binding(
				config.getStructureData().get(structureId).getDefaultCheckDistance(),
				() -> config.getStructureData().get(structureId).getFlatnessCheckDistance(),
				flatnessCheckDistance -> config.getStructureData().get(structureId).setFlatnessCheckDistance(flatnessCheckDistance)
			)
			.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 256).step(1)).build();

		structureCategoryBuilder.option(flatnessCheckDistanceOption);

		var flatnessCheckThresholdOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.flatness_check_threshold.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.flatness_check_threshold.description")))
			.binding(
				StructureData.FLATNESS_CHECK_THRESHOLD_DEFAULT_VALUE,
				() -> config.getStructureData().get(structureId).getFlatnessCheckThreshold(),
				flatnessCheckThreshold -> config.getStructureData().get(structureId).setFlatnessCheckThreshold(flatnessCheckThreshold)
			)
			.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 256).step(1)).build();

		structureCategoryBuilder.option(flatnessCheckThresholdOption);

		var allowAirBlocksInFlatnessCheckOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.allow_air_blocks_in_flatness_check.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.allow_air_blocks_in_flatness_check.description")))
			.binding(
				StructureData.ALLOW_AIR_BLOCKS_IN_FLATNESS_CHECK_DEFAULT_VALUE,
				() -> config.getStructureData().get(structureId).areAirBlocksAllowedInFlatnessCheck(),
				allowAirBlocksInFlatnessCheck -> config.getStructureData().get(structureId).setAllowAirBlocksInFlatnessCheck(allowAirBlocksInFlatnessCheck)
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
				.coloured(true)).build();

		structureCategoryBuilder.option(allowAirBlocksInFlatnessCheckOption);

		var allowLiquidBlocksInFlatnessCheckOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.allow_liquid_blocks_in_flatness_check.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.allow_liquid_blocks_in_flatness_check.description")))
			.binding(
				StructureData.ALLOW_LIQUID_BLOCKS_IN_FLATNESS_CHECK_DEFAULT_VALUE,
				() -> config.getStructureData().get(structureId).areLiquidBlocksAllowedInFlatnessCheck(),
				allowLiquidBlocksInFlatnessCheck -> config.getStructureData().get(structureId).setAllowLiquidBlocksInFlatnessCheck(allowLiquidBlocksInFlatnessCheck)
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
				.coloured(true)).build();

		structureCategoryBuilder.option(allowLiquidBlocksInFlatnessCheckOption);

		structureCategoryBuilder.option(LabelOption.create(Component.translatable("gui.structurify.structures.biome_check_group.title")));

		var enableBiomeCheckOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.enable_biome_check.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.enable_biome_check.description")))
			.binding(
				StructureData.ENABLE_BIOME_CHECK_DEFAULT_VALUE,
				() -> config.getStructureData().get(structureId).isBiomeCheckEnabled(),
				enableBiomeCheck -> config.getStructureData().get(structureId).setEnableBiomeCheck(enableBiomeCheck)
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
				.coloured(true)).build();

		structureCategoryBuilder.option(enableBiomeCheckOption);

		var biomeCheckDistanceOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.biome_check_distance.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.biome_check_distance.description")))
			.binding(
				config.getStructureData().get(structureId).getDefaultCheckDistance(),
				() -> config.getStructureData().get(structureId).getBiomeCheckDistance(),
				biomeCheckDistance -> config.getStructureData().get(structureId).setBiomeCheckDistance(biomeCheckDistance)
			)
			.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 256).step(1)).build();

		structureCategoryBuilder.option(biomeCheckDistanceOption);

		var biomeCheckModeOption = Option.<StructureData.BiomeCheckMode>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.biome_check_mode.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.biome_check_mode.description")))
			.binding(
				StructureData.BIOME_CHECK_MODE_DEFAULT_VALUE,
				() -> config.getStructureData().get(structureId).getBiomeCheckMode(),
				biomeCheckMode -> config.getStructureData().get(structureId).setBiomeCheckMode(biomeCheckMode)
			).controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(StructureData.BiomeCheckMode.class)
				.valueFormatter(biomeCheckMode -> Component.translatable("gui.structurify.structures.structure.biome_check_mode." + biomeCheckMode.name().toLowerCase()))).build();

		structureCategoryBuilder.option(biomeCheckModeOption);

		var biomeCheckBlacklistedBiomesOption = ListOption.<String>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.biome_check_blacklisted_biomes.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.biome_check_blacklisted_biomes.description")))
			.insertEntriesAtEnd(false)
			.binding(
				StructureData.BIOME_CHECK_BLACKLISTED_BIOMES_DEFAULT_VALUE,
				() -> config.getStructureData().get(structureId).getBiomeCheckBlacklistedBiomes(),
				blacklistedBiomes -> config.getStructureData().get(structureId).setBiomeCheckBlacklistedBiomes(blacklistedBiomes)
			)
			.controller(BiomeStringControllerBuilder::create)
			.available(config.getStructureData().get(structureId).getBiomeCheckMode() == StructureData.BiomeCheckMode.BLACKLIST)
			.initial("").build();

		structureCategoryBuilder.group(biomeCheckBlacklistedBiomesOption);

		biomeCheckModeOption.addListener((opt, biomeCheckMode) -> {
			boolean isBlacklistAvailable = biomeCheckMode == StructureData.BiomeCheckMode.BLACKLIST;
			biomeCheckBlacklistedBiomesOption.setAvailable(isBlacklistAvailable);
		});

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

		yacl.category(structureCategoryBuilder.build());

		return (YACLScreen) yacl.build().generateScreen(parent);
	}
}