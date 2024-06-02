package com.faboslav.structurify.common.config.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DropdownStringControllerBuilder;
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
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());

		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Text.literal(structureId))
			.save(Structurify.getConfig()::save);

		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Text.literal(structureId))
			.tooltip(Text.literal("Specific structure settings"));

		/*
		var structureBlacklistedBiomesGroup = OptionGroup.createBuilder()
			.name(Text.translatable("gui.structurify.structures.general.title"))
			.description(OptionDescription.of(Text.literal("gui.structurify.structures.general.description")));
		*/
		List<String> translatedBiomes = WorldgenDataProvider.getBiomes().stream()
			.map(biome -> LanguageUtil.translateId("biome", biome).getString())
			.toList();

		var blackListedBiomesOption = ListOption.<String>createBuilder()
			.name(Text.translatable("gui.structurify.structures.structure.blacklisted_biomes.title"))
			.description(OptionDescription.of(Text.translatable("gui.structurify.structures.structure.blacklisted_biomes.description")))
			.binding(
				new ArrayList<>(),
				() -> Structurify.getConfig().getStructureData().get(structureId).getBlacklistedBiomes(),
				blacklistedBiomes ->  Structurify.getConfig().getStructureData().get(structureId).setBlacklistedBiomes(blacklistedBiomes)
			)
			.controller(opt -> DropdownStringControllerBuilder.create(opt).values(translatedBiomes)) // usual controllers, passed to every entry
			.initial(""); // when adding a new entry to the list, this is the initial value it has

		//structureBlacklistedBiomesGroup.option(blackListedBiomesOption.build());
		structureCategoryBuilder.group(blackListedBiomesOption.build());

		yacl.category(structureCategoryBuilder.build());

		return yacl.build().generateScreen(parent);
	}
}