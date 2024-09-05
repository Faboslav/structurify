package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.client.api.controller.builder.BiomeStringControllerBuilder;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class StructureConfigScreen
{
	public static Screen create(Screen parent, String structureId) {
		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Component.literal(structureId))
			.save(Structurify.getConfig()::save);

		var translatedStructureName = LanguageUtil.translateId("structure", structureId);
		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.title", translatedStructureName))
			.tooltip(Component.translatable("gui.structurify.structures.structure.description"));

		var enableBiomeCheckOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.enable_biome_check.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.enable_biome_check.description")))
			.binding(
				false,
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
				Structurify.getConfig().getStructureData().get(structureId).getDefaultBiomeCheckDistance(),
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

		return yacl.build().generateScreen(parent);
	}
}