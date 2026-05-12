package com.faboslav.structurify.common.config.client.gui.structure;

import com.faboslav.structurify.common.config.client.gui.structure.jigsaw.HeightProviderOptions;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.structure.JigsawData;
import com.faboslav.structurify.common.config.data.structure.jigsaw.ProjectStartToHeightmap;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public final class JigsawOptions
{
	public static ArrayList<Option<?>> addJigsawOptions(StructureData structureData, String structureId) {
		var jigsawData = structureData.getJigsawData();
		var jigsawOptions = new ArrayList<Option<?>>();

		if(jigsawData.isUsingSize()) {
			var sizeOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.jigsaw.size.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.jigsaw.size.description")))
				.binding(
					jigsawData.getDefaultSize(),
					jigsawData::getSize,
					jigsawData::setSize
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(JigsawData.MIN_SIZE, JigsawData.MAX_SIZE).step(1)).build();

			jigsawOptions.add(sizeOption);
		}

		if(jigsawData.isUsingMaxDistanceFromCenter()) {
			//? if >=1.21.9 {
			var horizontalMaxDistanceFromCenterOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.jigsaw.horizontal_max_distance_from_center.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.jigsaw.horizontal_max_distance_from_center.description")))
				.available(!structureData.isDisabled())
				.binding(
					jigsawData.getDefaultHorizontalMaxDistanceFromCenter(),
					jigsawData::getHorizontalMaxDistanceFromCenter,
					jigsawData::setHorizontalMaxDistanceFromCenter
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(JigsawData.MIN_HORIZONTAL_MAX_DISTANCE_FROM_CENTER, JigsawData.MAX_HORIZONTAL_MAX_DISTANCE_FROM_CENTER).step(1)).build();

			jigsawOptions.add(horizontalMaxDistanceFromCenterOption);

			var verticalMaxDistanceFromCenterOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.jigsaw.vertical_max_distance_from_center.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.jigsaw.vertical_max_distance_from_center.description")))
				.available(!structureData.isDisabled())
				.binding(
					jigsawData.getDefaultVerticalMaxDistanceFromCenter(),
					jigsawData::getVerticalMaxDistanceFromCenter,
					jigsawData::setVerticalMaxDistanceFromCenter
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(JigsawData.MIN_VERTICAL_MAX_DISTANCE_FROM_CENTER, JigsawData.MAX_VERTICAL_MAX_DISTANCE_FROM_CENTER).step(1)).build();

			jigsawOptions.add(verticalMaxDistanceFromCenterOption);
			//?} else {
			/*var maxDistanceFromCenterOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.jigsaw.max_distance_from_center.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.jigsaw.max_distance_from_center.description")))
				.available(!structureData.isDisabled())
				.binding(
					jigsawData.getDefaultHorizontalMaxDistanceFromCenter(),
					jigsawData::getHorizontalMaxDistanceFromCenter,
					maxDistanceFromCenter -> {
						jigsawData.setHorizontalMaxDistanceFromCenter(maxDistanceFromCenter);
						jigsawData.setVerticalMaxDistanceFromCenter(maxDistanceFromCenter);
					}
				)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(JigsawData.MIN_HORIZONTAL_MAX_DISTANCE_FROM_CENTER, JigsawData.MAX_HORIZONTAL_MAX_DISTANCE_FROM_CENTER).step(1)).build();

			jigsawOptions.add(maxDistanceFromCenterOption);
			*///?}
		}

		if(jigsawData.isUsingHeightProvider()) {
			var heightProviderOptions = HeightProviderOptions.addHeightProviderOptions(jigsawData, structureId);
			jigsawOptions.addAll(heightProviderOptions);
		}

		if(jigsawData.isUsingProjectStartToHeightmap()) {
			var projectStartToHeightmapOption = Option.<ProjectStartToHeightmap>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.jigsaw.project_start_to_heightmap.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.jigsaw.project_start_to_heightmap.description")))
				.binding(
					jigsawData.getDefaultProjectStartToHeightmap(),
					() -> jigsawData.getProjectStartToHeightmap(),
					jigsawData::setProjectStartToHeightmap
				).controller(opt -> EnumControllerBuilder.create(opt)
					.enumClass(ProjectStartToHeightmap.class)
					.formatValue(projectStartToHeightmap -> Component.translatable(LanguageUtil.getHumanReadableName(projectStartToHeightmap.name().toLowerCase())))).build();

			jigsawOptions.add(projectStartToHeightmapOption);
		}

		return jigsawOptions;
	}
}
