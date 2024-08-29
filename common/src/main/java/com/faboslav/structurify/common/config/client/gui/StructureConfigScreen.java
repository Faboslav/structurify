package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.client.api.controller.builder.BiomeStringControllerBuilder;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.title", LanguageUtil.translateId("structure", structureId)))
			.tooltip(Component.translatable("gui.structurify.structures.structure.description"));

		/*
		var structureBlacklistedBiomesGroup = OptionGroup.createBuilder()
			.name(Text.translatable("gui.structurify.structures.general.title"))
			.description(OptionDescription.of(Text.literal("gui.structurify.structures.general.description")));
		*/

		var biomeBlacklistTypeOption = Option.<StructureData.BiomeBlacklistType>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.biome_blacklist_type.title"))
			.binding(
				StructureData.DEFAULT_BIOME_BLACKLIST_TYPE,
				() -> Structurify.getConfig().getStructureData().get(structureId).getBiomeBlacklistType(),
				biomeBlacklistType -> Structurify.getConfig().getStructureData().get(structureId).setBiomeBlacklistType(biomeBlacklistType)
			).controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(StructureData.BiomeBlacklistType.class)
				.valueFormatter(v -> Component.translatable("gui.structurify.structures.structure.biome_blacklist_type." + v.name().toLowerCase())))
			.available(!Structurify.getConfig().getStructureData().get(structureId).isBiomeBlacklistTypeLocked());

		var biomeBlacklistTypeDescription = OptionDescription.of(Component.translatable("gui.structurify.structures.structure.biome_blacklist_type.title"));

		biomeBlacklistTypeOption.description(biomeBlacklistTypeDescription);

		List<String> biomes = WorldgenDataProvider.getBiomes().stream().toList();

		/*
		Map<String, String> biomeTranslatedBiomePair = WorldgenDataProvider.getBiomes().stream()
			.collect(Collectors.toMap(
				biome -> biome,
				biome -> LanguageUtil.translateId("biome", biome).getString()
			));

		Map<String, String> translatedBiomeBiomePair = translatedBiomesMap.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getValue,
				Map.Entry::getKey
			));
			*/

		var blackListedBiomesOption = ListOption.<String>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.blacklisted_biomes.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.blacklisted_biomes.description")))
			.insertEntriesAtEnd(true)
			.binding(
				new ArrayList<>(),
				() -> Structurify.getConfig().getStructureData().get(structureId).getBlacklistedBiomes(),
				blacklistedBiomes -> Structurify.getConfig().getStructureData().get(structureId).setBlacklistedBiomes(blacklistedBiomes)
			)
			.controller(BiomeStringControllerBuilder::create)
			.initial("").build();


		//DropdownStringControllerBuilder
		structureCategoryBuilder.option(biomeBlacklistTypeOption.build());
		structureCategoryBuilder.group(blackListedBiomesOption);

		yacl.category(structureCategoryBuilder.build());

		return yacl.build().generateScreen(parent);
	}
}