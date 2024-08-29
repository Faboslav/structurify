package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.StructureButtonControllerBuilder;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class StructuresConfigScreen
{
	public static Screen createConfigGui(StructurifyConfig config, Screen parent) {
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());

		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Component.translatable("gui.structurify.title"))
			.save(config::save);

		createStructuresTab(yacl, config);

		return yacl.build().generateScreen(parent);
	}

	public static void createStructuresTab(YetAnotherConfigLib.Builder yacl, StructurifyConfig config) {
		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structures_category.title"))
			.tooltip(Component.translatable("gui.structurify.structures_category.description"));

		var generalStructuresGroupBuilder = OptionGroup.createBuilder()
			.name(Component.translatable("gui.structurify.structures.general.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.general.description")));

		var disableAllStructuresOptionBuilder = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.disable_all_structures.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.disable_all_structures.description")))
			.binding(
				false,
				() -> config.disableAllStructures,
				disableAllStructures -> config.disableAllStructures = disableAllStructures
			)
			.controller(opt -> BooleanControllerBuilder.create(opt).valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes").withStyle(style -> style.withColor(ChatFormatting.RED)):Component.translatable("gui.structurify.label.no").withStyle(style -> style.withColor(ChatFormatting.GREEN))));

		generalStructuresGroupBuilder.option(disableAllStructuresOptionBuilder.build());
		structureCategoryBuilder.group(generalStructuresGroupBuilder.build());

		var structures = WorldgenDataProvider.getStructures();
		List<OptionGroup> optionGroups = new ArrayList<>();
		OptionGroup.Builder currentGroupBuilder = null;
		String currentNamespace = null;

		for (Map.Entry<String, StructureData> entry : structures.entrySet()) {
			String structureStringId = entry.getKey();
			StructureData structureData = entry.getValue();

			ResourceLocation structureId = Structurify.makeVanillaId(structureStringId);
			String namespace = structureId.getNamespace();

			// Create new group for each namespace
			if (!namespace.equals(currentNamespace)) {
				if (currentGroupBuilder != null) {
					optionGroups.add(currentGroupBuilder.build());
				}

				// Create new group
				currentGroupBuilder = OptionGroup.createBuilder()
					.name(Component.translatable("gui.structurify.structures.structures_group.title", LanguageUtil.translateId(null, namespace).getString()))
					.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structures_group.description", namespace)));

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
					.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.enabled"):Component.translatable("gui.structurify.label.disabled"))
					.coloured(true));

			var descriptionBuilder = OptionDescription.createBuilder();

			descriptionBuilder.text(Component.translatable("gui.structurify.structures.biomes_description").append(Component.literal("\n")));

			for (String biome : structureData.getBiomes()) {
				descriptionBuilder.text(Component.literal(" - ").append(LanguageUtil.translateId("biome", biome)));
			}

			descriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structures.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

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
}