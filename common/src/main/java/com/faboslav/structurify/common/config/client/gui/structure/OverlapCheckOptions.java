package com.faboslav.structurify.common.config.client.gui.structure;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.data.StructureLikeData;
import com.faboslav.structurify.common.config.data.structure.OverlapCheckData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionAddable;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.network.chat.Component;

import java.util.Map;

public final class OverlapCheckOptions
{
	public static void addOverlapCheckOptions(
		OptionAddable builder,
		StructurifyConfig config,
		String id
	) {
		boolean isNamespace = !id.contains(":");
		String namespace;
		boolean isEnabledForNamespace = config.getStructureNamespaceData().get(id.split(":")[0]).getOverlapCheckData().isExcludedFromOverlapPrevention();
		Map<String, ? extends StructureLikeData> structureLikeData;

		if (isNamespace) {
			namespace = id;
			structureLikeData = config.getStructureNamespaceData();
		} else {
			namespace = id.split(":")[0];
			structureLikeData = config.getStructureData();
		}

		var overlapCheckData = structureLikeData.get(id).getOverlapCheckData();
		var title = Component.translatable("gui.structurify.structures.overlap_check_group.title");

		if (isNamespace) {
			title = Component.literal("„" + LanguageUtil.translateId(null, namespace).getString() + "“ ").append(title);
		}

		title = Component.literal("\n").append(title);

		builder.option(LabelOption.create(title.withStyle(style -> style.withBold(true))));

		boolean isStructureOverlapPreventionEnabled = config.preventStructureOverlap;

		Option<Boolean> excludeFromOverlapPreventionOption = Option.<Boolean>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.exclude_from_overlap_prevention.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.exclude_from_overlap_prevention.description", namespace, id)))
				.available(isStructureOverlapPreventionEnabled && !isEnabledForNamespace)
				.binding(
					OverlapCheckData.IS_EXCLUDED_FROM_OVERLAP_PREVENTION_DEFAULT_VALUE,
					overlapCheckData::isExcludedFromOverlapPrevention,
					overlapCheckData::excludeFromOverlapPrevention
				).controller(opt -> BooleanControllerBuilder.create(opt)
					.formatValue(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
					.coloured(true)
			).build();

			builder.option(excludeFromOverlapPreventionOption);
	}
}
