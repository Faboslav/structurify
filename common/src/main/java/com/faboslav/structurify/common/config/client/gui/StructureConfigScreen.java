package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DropdownStringControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.gui.controllers.cycling.EnumController;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class StructureConfigScreen
{
	public static Screen create(Screen parent, String structureId) {
		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Text.literal(structureId))
			.save(Structurify.getConfig()::save);

		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(LanguageUtil.translateId("structure", structureId))
			.tooltip(Text.literal("Specific structure settings"));

		/*
		var structureBlacklistedBiomesGroup = OptionGroup.createBuilder()
			.name(Text.translatable("gui.structurify.structures.general.title"))
			.description(OptionDescription.of(Text.literal("gui.structurify.structures.general.description")));
		*/
		List<String> translatedBiomes = WorldgenDataProvider.getBiomes().stream()
			.map(biome -> LanguageUtil.translateId("biome", biome).getString())
			.toList();

		var biomeBlacklistTypeOption = Option.<StructureData.BiomeBlacklistType>createBuilder()
									 .name(Text.translatable("gui.structurify.structures.structure.biome_blacklist_type.title"))
									 .binding(
										 StructureData.BiomeBlacklistType.CENTER_PART,
										 () -> Structurify.getConfig().getStructureData().get(structureId).getBiomeBlacklistType(),
										 biomeBlacklistType -> Structurify.getConfig().getStructureData().get(structureId).setBiomeBlacklistType(biomeBlacklistType)
									 ).controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(StructureData.BiomeBlacklistType.class)
				.valueFormatter(v -> Text.translatable("gui.structurify.structures.structure.biome_blacklist_type." + v.name().toLowerCase())));

		var biomeBlacklistTypeDescription = OptionDescription.of(Text.translatable("gui.structurify.structures.structure.biome_blacklist_type.title"));

		biomeBlacklistTypeOption.description(biomeBlacklistTypeDescription);

		var blackListedBiomesOption = ListOption.<String>createBuilder()
			.name(Text.translatable("gui.structurify.structures.structure.blacklisted_biomes.title"))
			.description(OptionDescription.of(Text.translatable("gui.structurify.structures.structure.blacklisted_biomes.description")))
			.insertEntriesAtEnd(true)
			.binding(
				new ArrayList<>(),
				() -> Structurify.getConfig().getStructureData().get(structureId).getBlacklistedBiomes(),
				blacklistedBiomes -> Structurify.getConfig().getStructureData().get(structureId).setBlacklistedBiomes(blacklistedBiomes)
			)
			.controller(opt -> DropdownStringControllerBuilder.create(opt).values(translatedBiomes)) // usual controllers, passed to every entry
			.initial(""); // when adding a new entry to the list, this is the initial value it has

		structureCategoryBuilder.option(biomeBlacklistTypeOption.build());
		structureCategoryBuilder.group(blackListedBiomesOption.build());

		yacl.category(structureCategoryBuilder.build());

		return yacl.build().generateScreen(parent);
	}
}