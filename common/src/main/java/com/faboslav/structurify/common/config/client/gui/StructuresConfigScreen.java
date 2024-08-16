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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class StructuresConfigScreen
{
	public static Screen createConfigGui(StructurifyConfig config, Screen parent) {
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());

		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Text.translatable("gui.structurify.title"))
			.save(config::save);

		createStructuresTab(yacl, config);

		return yacl.build().generateScreen(parent);
	}

	public static void createStructuresTab(YetAnotherConfigLib.Builder yacl, StructurifyConfig config) {
		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Text.translatable("gui.structurify.structures_category.title"))
			.tooltip(Text.translatable("gui.structurify.structures_category.description"));

		var generalStructuresGroupBuilder = OptionGroup.createBuilder()
			.name(Text.translatable("gui.structurify.structures.general.title"))
			.description(OptionDescription.of(Text.translatable("gui.structurify.structures.general.description")));

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

			Identifier structureId = Structurify.makeVanillaId(structureStringId);
			String namespace = structureId.getNamespace();

			// Create new group for each namespace
			if (!namespace.equals(currentNamespace)) {
				if (currentGroupBuilder != null) {
					optionGroups.add(currentGroupBuilder.build());
				}

				// Create new group
				currentGroupBuilder = OptionGroup.createBuilder()
					.name(Text.translatable("gui.structurify.structures.structures_group.title", LanguageUtil.translateId(null, namespace).getString()))
					.description(OptionDescription.of(Text.translatable("gui.structurify.structures.structures_group.description", namespace)));

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
}