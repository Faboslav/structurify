package com.faboslav.structurify.common.config.client.gui.structure.jigsaw;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.StructurifyClient;
import com.faboslav.structurify.common.config.client.api.controller.builder.DualControllerBuilder;
import com.faboslav.structurify.common.config.client.api.option.HolderOption;
import com.faboslav.structurify.common.config.client.api.option.OptionPair;
import com.faboslav.structurify.common.config.client.gui.StructureConfigScreen;
import com.faboslav.structurify.common.config.client.gui.StructureTemplatePoolsConfigScreen;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.structure.DistanceFromWorldCenterCheckData;
import com.faboslav.structurify.common.config.data.structure.JigsawData;
import com.faboslav.structurify.common.config.data.structure.jigsaw.HeightProviderData;
import com.faboslav.structurify.common.config.data.structure.jigsaw.VerticalAnchorData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class HeightProviderOptions
{
	public static ArrayList<Option<?>> addHeightProviderOptions(JigsawData jigsawData, String structureId) {
		var defaultHeightProviderData = jigsawData.getDefaultHeightProviderData();
		var heightProviderData = jigsawData.getHeightProviderData();

		if(defaultHeightProviderData == null || heightProviderData == null) {
			return new ArrayList<>();
		}

		var heightProviderOptions = new ArrayList<Option<?>>();

		var heightProviderTypeOption = Option.<HeightProviderData.Type>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.description")))
			.binding(
				defaultHeightProviderData.getType(),
				heightProviderData::getType,
				heightProviderData::setType
			)
			.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(HeightProviderData.Type.class)
				.formatValue(step -> Component.translatable(LanguageUtil.getHumanReadableName(step.name().toLowerCase()))))
			.build();

		heightProviderOptions.add(heightProviderTypeOption);

		if(heightProviderData.getType() == HeightProviderData.Type.CONSTANT) {
			var defaultValueData = defaultHeightProviderData.getValue();
			var valueData = heightProviderData.getValue();

			var verticalAnchorOptions = addConstantVerticalAnchorOptions(defaultValueData, valueData, Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.height_value.title"), Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.height_value.description"));
			heightProviderOptions.add(verticalAnchorOptions);
		} else {
			var defaultMinInclusiveData = defaultHeightProviderData.getMinInclusive();
			var minInclusiveData = heightProviderData.getMinInclusive();

			var minInclusiveDataOptions = addVerticalAnchorOptions(defaultMinInclusiveData, minInclusiveData, Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.min_inclusive.title"), Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.min_inclusive.description"));
			heightProviderOptions.add(minInclusiveDataOptions);

			var defaultMaxInclusiveData = defaultHeightProviderData.getMaxInclusive();
			var maxInclusiveData = heightProviderData.getMaxInclusive();

			var maxInclusiveDataOptions = addVerticalAnchorOptions(defaultMaxInclusiveData, maxInclusiveData, Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.max_inclusive.title"), Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.max_inclusive.description"));
			heightProviderOptions.add(maxInclusiveDataOptions);
		}

		if(heightProviderData.getType() == HeightProviderData.Type.TRAPEZOID) {
			var plateauOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.trapezoid.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.trapezoid.description")))
				.binding(
					defaultHeightProviderData.getPlateau(),
					heightProviderData::getPlateau,
					heightProviderData::setPlateau
				)
				.controller(IntegerFieldControllerBuilder::create)
				.build();

			heightProviderOptions.add(plateauOption);
		}

		if(heightProviderData.getType() == HeightProviderData.Type.BIASED_TO_BOTTOM || heightProviderData.getType() == HeightProviderData.Type.VERY_BIASED_TO_BOTTOM) {
			var innerOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.inner.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.jigsaw.start_height.trapezoid.description")))
				.binding(
					defaultHeightProviderData.getInner(),
					heightProviderData::getInner,
					heightProviderData::setInner
				)
				.controller(IntegerFieldControllerBuilder::create)
				.build();

			heightProviderOptions.add(innerOption);
		}

		heightProviderTypeOption.addListener((opt, type) -> {
			var configScreen = StructurifyClient.getConfigScreen();

			if (configScreen == null) {
				return;
			}

			configScreen.currentScreen.finishOrSave();
			heightProviderData.setType(type);
			YACLScreen structureScreen = StructureConfigScreen.create(Structurify.getConfig(), structureId, configScreen.previousScreen);

			Minecraft.getInstance().setScreen(structureScreen);
			configScreen.loadScreenState(structureScreen);
		});

		/*
		var minStructureDistanceFromWorldOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.min_distance_from_world_center.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.min_distance_from_world_center.description")))
			.available(true)
			.binding(
				DistanceFromWorldCenterCheckData.MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE,
				heightProviderData.getMinInclusive().getType()
				distanceFromWorldCenterData::setMinDistanceFromWorldCenter
			)
			.controller(opt -> IntegerFieldControllerBuilder.create(opt).range(0, 100000)).build();

		var maxStructureDistanceFromWorldOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.max_distance_from_world_center.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.max_distance_from_world_center.description")))
			.available(isGlobal || distanceFromWorldCenterData.isOverridingGlobalDistanceFromWorldCenter() || (!isEnabledGlobally && !isEnabledForNamespace))
			.binding(
				DistanceFromWorldCenterCheckData.MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE,
				distanceFromWorldCenterData::getMaxDistanceFromWorldCenter,
				distanceFromWorldCenterData::setMaxDistanceFromWorldCenter
			)
			.controller(opt -> IntegerFieldControllerBuilder.create(opt).range(0, 100000)).build();

		var distanceFromWorldCenterPair = new OptionPair<>(minStructureDistanceFromWorldOption, maxStructureDistanceFromWorldOption);

		var distanceFromWorldCenterOption =
			HolderOption.<Option<Integer>, Option<Integer>>createBuilder()
				.optionPair(distanceFromWorldCenterPair)
				.controller(opt -> DualControllerBuilder.create(distanceFromWorldCenterPair))
				.build();

		distanceFromWorldCenterOptions.put(DISTANCE_FROM_WORLD_CENTER_OPTION_NAME, distanceFromWorldCenterOption);

		builder.option(distanceFromWorldCenterOption);*/

		return heightProviderOptions;
	}

	private static Option<?> addVerticalAnchorOptions(
		VerticalAnchorData defaultVerticalAnchorData,
		VerticalAnchorData verticalAnchorData,
		Component title,
		Component description
	) {
		var verticalAnchorTypeOption = Option.<VerticalAnchorData.Type>createBuilder()
			.name(title)
			.description(OptionDescription.of(description))
			.binding(
				VerticalAnchorData.Type.fromDataType(defaultVerticalAnchorData.getType()),
				() -> VerticalAnchorData.Type.fromDataType(verticalAnchorData.getType()),
				type -> verticalAnchorData.setType(type.toDataType())
			)
			.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(VerticalAnchorData.Type.class)
				.formatValue(step -> Component.translatable(LanguageUtil.getHumanReadableName(step.name().toLowerCase()))))
			.build();

		return addVerticalAnchorOption(verticalAnchorTypeOption, defaultVerticalAnchorData, verticalAnchorData);
	}

	private static Option<?> addConstantVerticalAnchorOptions(
		VerticalAnchorData defaultVerticalAnchorData,
		VerticalAnchorData verticalAnchorData,
		Component title,
		Component description
	) {
		var verticalAnchorTypeOption = Option.<VerticalAnchorData.ConstantVerticalAnchorType>createBuilder()
			.name(title)
			.description(OptionDescription.of(description))
			.binding(
				VerticalAnchorData.ConstantVerticalAnchorType.fromDataType(defaultVerticalAnchorData.getType()),
				() -> VerticalAnchorData.ConstantVerticalAnchorType.fromDataType(verticalAnchorData.getType()),
				type -> verticalAnchorData.setType(type.toDataType())
			)
			.controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(VerticalAnchorData.ConstantVerticalAnchorType.class)
				.formatValue(step -> Component.translatable(LanguageUtil.getHumanReadableName(step.name().toLowerCase()))))
			.build();

		return addVerticalAnchorOption(verticalAnchorTypeOption, defaultVerticalAnchorData, verticalAnchorData);
	}

	private static <T extends Enum<T>> Option<?> addVerticalAnchorOption(
		Option<T> verticalAnchorTypeOption,
		VerticalAnchorData defaultVerticalAnchorData,
		VerticalAnchorData verticalAnchorData
	) {
		var heightProviderValueOption = Option.<Integer>createBuilder()
			.name(Component.empty())
			.description(OptionDescription.of(Component.empty()))
			.binding(
				defaultVerticalAnchorData.getValue() == null ? 0 : defaultVerticalAnchorData.getValue(),
				() -> verticalAnchorData.getValue() == null ? 0 : verticalAnchorData.getValue(),
				verticalAnchorData::setValue
			)
			.controller(IntegerFieldControllerBuilder::create)
			.available(verticalAnchorData.getType() != VerticalAnchorData.Type.TOP && verticalAnchorData.getType() != VerticalAnchorData.Type.BOTTOM)
			.build();

		verticalAnchorTypeOption.addListener((opt, type) -> {
			boolean hasValue = verticalAnchorData.getType() != VerticalAnchorData.Type.TOP && verticalAnchorData.getType() != VerticalAnchorData.Type.BOTTOM;
			heightProviderValueOption.setAvailable(hasValue);
		});

		var verticalAnchorPair = new OptionPair<>(verticalAnchorTypeOption, heightProviderValueOption);

		return HolderOption.<Option<T>, Option<Integer>>createBuilder()
			.optionPair(verticalAnchorPair)
			.controller(opt -> DualControllerBuilder.create(verticalAnchorPair))
			.build();
	}
}
