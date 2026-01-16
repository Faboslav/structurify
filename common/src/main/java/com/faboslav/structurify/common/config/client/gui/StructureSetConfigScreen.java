package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.option.InvisibleOptionGroup;
import com.faboslav.structurify.common.config.client.gui.structure.*;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.structure.JigsawData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public final class StructureSetConfigScreen
{
	public static YACLScreen create(StructurifyConfig config, String structureSetId, Screen parent) {
		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Component.literal(structureSetId))
			.save(config::save);

		var structureSetData = config.getStructureSetData().get(structureSetId);
		var translatedStructureSetName = LanguageUtil.translateId("structure", structureSetId);
		var structureCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure_set.title", translatedStructureSetName))
			.tooltip(Component.translatable("gui.structurify.structures.structure_set.description", translatedStructureSetName));

		var structureSetSettingsGroup = new InvisibleOptionGroup.Builder().name(Component.literal("test"));
		structureSetSettingsGroup.option(LabelOption.create(Component.translatable("gui.structurify.structures.structure_sets.structure_set.title").withStyle(style -> style.withBold(true))));

		var isDisabledOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.is_disabled.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.is_disabled.description")))
			.binding(
				StructureSetData.IS_DISABLED_DEFAULT_VALUE,
				structureSetData::isDisabled,
				structureSetData::setDisabled
			)
			.controller(opt -> BooleanControllerBuilder.create(opt).formatValue(val -> val ? Component.translatable("gui.structurify.label.yes").withStyle(style -> style.withColor(ChatFormatting.RED)):Component.translatable("gui.structurify.label.no").withStyle(style -> style.withColor(ChatFormatting.GREEN)))).build();

		structureSetSettingsGroup.option(isDisabledOption);

		structureSetSettingsGroup.option(LabelOption.create(Component.translatable("gui.structurify.structures.structure_sets.structure_set.weights.title").withStyle(style -> style.withBold(true))));

		for(var structureWeight : structureSetData.getStructureWeights().entrySet()) {
			var structureId = structureWeight.getKey();
			var translatedStructureName = LanguageUtil.translateId("structure", structureId);

			var weightOption = Option.<Integer>createBuilder()
				.name(translatedStructureName)
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.jigsaw.size.description")))
				.binding(
					structureSetData.getDefaultStructureWeights().get(structureId),
					() -> structureSetData.getStructureWeights().get(structureId),
					(weight) -> structureSetData.getStructureWeights().put(structureId, weight)
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(StructureSetData.MIN_STRUCTURE_WEIGHT, StructureSetData.MAX_STRUCTURE_WEIGHT).step(1)).build();

			structureSetSettingsGroup.option(weightOption);
		}

		structureCategoryBuilder.group(structureSetSettingsGroup.build());
		yacl.category(structureCategoryBuilder.build());

		return (YACLScreen) yacl.build().generateScreen(parent);
	}
}