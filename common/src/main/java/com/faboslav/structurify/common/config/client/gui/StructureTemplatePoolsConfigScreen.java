package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.option.InvisibleOptionGroup;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.StructureTemplatePoolData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Map;

public final class StructureTemplatePoolsConfigScreen
{
	public static YACLScreen create(StructurifyConfig config, String structureId, Screen parent) {
		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Component.literal(structureId).append("_template_pools"))
			.save(config::save);

		var translatedStructureName = LanguageUtil.translateId("structure", structureId);
		var structureTemplatePoolCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.template_pools.title", translatedStructureName))
			.tooltip(Component.translatable("gui.structurify.structures.structure.template_pools.description", translatedStructureName));
		Map<String, StructureTemplatePoolData> structureTemplatePools = config.getStructureTemplatePoolsDataForStructure(structureId);

		for(var templatePool : structureTemplatePools.entrySet()) {
			var templatePoolId = templatePool.getKey();
			var translatedTemplatePoolName = LanguageUtil.translateId("template_pool", templatePoolId);
			var templatePoolElements = templatePool.getValue().getStructureTemplatePoolElementWeights();

			var templatePoolSettingsGroup = new InvisibleOptionGroup.Builder().name(translatedTemplatePoolName);
			templatePoolSettingsGroup.option(LabelOption.create(Component.literal("\n").append(Component.translatable("gui.structurify.structures.structure.template_pools.template_pool.title", translatedTemplatePoolName).withStyle(style -> style.withBold(true)))));

			for(var templatePoolElement : templatePoolElements.entrySet()) {
				var templatePoolElementId = templatePoolElement.getKey();
				var translatedTemplatePoolElementName = LanguageUtil.translateId("template_pool_element", templatePoolElementId);

				var templatePoolElementWeightOption = Option.<Integer>createBuilder()
					.name(translatedTemplatePoolElementName)
					.description(OptionDescription.of(Component.translatable("gui.structurify.structure_sets.structure_set.weight.description")))
					.binding(
						templatePool.getValue().getDefaultStructureTemplatePoolElementWeights().get(templatePoolElementId),
						templatePoolElement::getValue,
						templatePoolElement::setValue
					)
					.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(StructureSetData.MIN_STRUCTURE_WEIGHT, StructureSetData.MAX_STRUCTURE_WEIGHT).step(1)).build();

				templatePoolSettingsGroup.option(templatePoolElementWeightOption);
			}

			structureTemplatePoolCategoryBuilder.group(templatePoolSettingsGroup.build());
		}

		yacl.category(structureTemplatePoolCategoryBuilder.build());
		return (YACLScreen) yacl.build().generateScreen(parent);
	}
}