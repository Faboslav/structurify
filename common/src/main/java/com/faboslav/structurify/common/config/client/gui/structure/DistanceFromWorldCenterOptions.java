package com.faboslav.structurify.common.config.client.gui.structure;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.DualControllerBuilder;
import com.faboslav.structurify.common.config.client.api.option.HolderOption;
import com.faboslav.structurify.common.config.client.api.option.OptionPair;
import com.faboslav.structurify.common.config.data.StructureLikeData;
import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import com.faboslav.structurify.common.config.data.structure.DistanceFromWorldCenterCheckData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionAddable;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DistanceFromWorldCenterOptions
{
	public static final String DISTANCE_FROM_WORLD_CENTER_CHECK_SYMBOL = "\u2316";
	public static final String OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME = "override_global_distance_from_world_center";
	public static final String DISTANCE_FROM_WORLD_CENTER_MODE_OPTION_NAME = "distance_from_world_center_mode";
	public static final String ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME = "enable_min_distance_from_world_center";
	public static final String ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME = "enable_max_distance_from_world_center";
	public static final String MIN_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME = "min_distance_from_world_center";
	public static final String MAX_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME = "max_distance_from_world_center";

	public static Map<String, Option<?>> addDistanceFromWorldCenterOptions(
		OptionAddable builder,
		StructurifyConfig config,
		String id
	) {
		boolean isEnabledGlobally = config.getStructureNamespaceData().get(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER).getDistanceFromWorldCenterCheckData().isEnabled();
		boolean isGlobal = id.equals(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER);
		boolean isEnabledForNamespace = config.getStructureNamespaceData().get(id.split(":")[0]).getDistanceFromWorldCenterCheckData().isEnabled();
		boolean isNamespace = !id.contains(":");

		String namespace;
		Map<String, ? extends StructureLikeData> structureLikeData;

		if (isNamespace) {
			namespace = id;
			structureLikeData = config.getStructureNamespaceData();
		} else {
			namespace = id.split(":")[0];
			structureLikeData = config.getStructureData();
		}

		var distanceFromWorldCenterOptions = new HashMap<String, Option<?>>();
		var distanceFromWorldCenterData = structureLikeData.get(id).getDistanceFromWorldCenterCheckData();
		boolean isEnabled = distanceFromWorldCenterData.isEnabled();

		var title = Component.translatable("gui.structurify.structures.distance_from_world_center_group.title");

		if (isGlobal || isNamespace) {
			title = Component.literal("„" + LanguageUtil.translateId(null, namespace).getString() + "“ ").append(title);
		}

		title = Component.literal("\n" + DISTANCE_FROM_WORLD_CENTER_CHECK_SYMBOL + " ").append(title);

		builder.option(LabelOption.create(title.withStyle(style -> style.withBold(true))));

		@Nullable Option<Boolean> isOverridingGlobalDistanceFromWorldCenterOption;

		if (!isGlobal) {
			isOverridingGlobalDistanceFromWorldCenterOption = Option.<Boolean>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.override_global_distance_from_world_center.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.override_global_distance_from_world_center.description", namespace, id)))
				.available(isEnabledGlobally || isEnabledForNamespace)
				.binding(
					DistanceFromWorldCenterCheckData.OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE,
					distanceFromWorldCenterData::isOverridingGlobalDistanceFromWorldCenter,
					distanceFromWorldCenterData::overrideGlobalDistanceFromWorldCenter
				).controller(opt -> BooleanControllerBuilder.create(opt)
					.formatValue(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
					.coloured(true)
				)
				.build();

			distanceFromWorldCenterOptions.put(OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME, isOverridingGlobalDistanceFromWorldCenterOption);
			builder.option(isOverridingGlobalDistanceFromWorldCenterOption);
		} else {
			isOverridingGlobalDistanceFromWorldCenterOption = null;
		}

		var modeOption = Option.<DistanceFromWorldCenterCheckData.DistanceFromWorldCenterCheckMode>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.distance_from_world_center_mode.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.distance_from_world_center_mode.description")))
			.available(isEnabled)
			.binding(
				DistanceFromWorldCenterCheckData.MODE_DEFAULT_VALUE,
				distanceFromWorldCenterData::getMode,
				distanceFromWorldCenterData::setMode
			).controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(DistanceFromWorldCenterCheckData.DistanceFromWorldCenterCheckMode.class)
				.formatValue(distanceFromWorldCenterMode -> Component.translatable("gui.structurify.structures.structure.distance_from_world_center_mode." + distanceFromWorldCenterMode.name().toLowerCase()))).build();

		distanceFromWorldCenterOptions.put(DISTANCE_FROM_WORLD_CENTER_MODE_OPTION_NAME, modeOption);
		builder.option(modeOption);

		var enableMinDistanceFromWorldCenterOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.enable_min_distance_from_world_center.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.enable_min_distance_from_world_center.description")))
			.available(isGlobal || distanceFromWorldCenterData.isOverridingGlobalDistanceFromWorldCenter() || (!isEnabledGlobally && !isEnabledForNamespace))
			.binding(
				DistanceFromWorldCenterCheckData.ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE,
				distanceFromWorldCenterData::isMinDistanceFromWorldCenterEnabled,
				distanceFromWorldCenterData::enableMinDistanceFromWorldCenter
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.formatValue(currentIsEnabled -> {
					if (currentIsEnabled) {
						if (isGlobal) {
							return Component.translatable("gui.structurify.label.yes_global");
						}

						if (isNamespace) {
							return Component.translatable("gui.structurify.label.yes_namespace", namespace);
						}

						return Component.translatable("gui.structurify.label.yes");
					}

					return Component.translatable("gui.structurify.label.no");
				})
				.coloured(true)
			)
			.build();

		distanceFromWorldCenterOptions.put(ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME, enableMinDistanceFromWorldCenterOption);

		var minStructureDistanceFromWorldOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.min_distance_from_world_center.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.min_distance_from_world_center.description")))
			.available(distanceFromWorldCenterData.isMinDistanceFromWorldCenterEnabled())
			.binding(
				DistanceFromWorldCenterCheckData.MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE,
				distanceFromWorldCenterData::getMinDistanceFromWorldCenter,
				distanceFromWorldCenterData::setMinDistanceFromWorldCenter
			)
			.controller(opt -> IntegerFieldControllerBuilder.create(opt).range(DistanceFromWorldCenterCheckData.DISTANCE_FROM_WORLD_CENTER_MIN_LIMIT, DistanceFromWorldCenterCheckData.DISTANCE_FROM_WORLD_CENTER_MAX_LIMIT)).build();

		distanceFromWorldCenterOptions.put(MIN_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME, minStructureDistanceFromWorldOption);

		var enableMaxDistanceFromWorldCenterOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.enable_max_distance_from_world_center.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.enable_max_distance_from_world_center.description")))
			.available(isGlobal || distanceFromWorldCenterData.isOverridingGlobalDistanceFromWorldCenter() || (!isEnabledGlobally && !isEnabledForNamespace))
			.binding(
				DistanceFromWorldCenterCheckData.ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE,
				distanceFromWorldCenterData::isMaxDistanceFromWorldCenterEnabled,
				distanceFromWorldCenterData::enableMaxDistanceFromWorldCenter
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.formatValue(currentIsEnabled -> {
					if (currentIsEnabled) {
						if (isGlobal) {
							return Component.translatable("gui.structurify.label.yes_global");
						}

						if (isNamespace) {
							return Component.translatable("gui.structurify.label.yes_namespace", namespace);
						}

						return Component.translatable("gui.structurify.label.yes");
					}

					return Component.translatable("gui.structurify.label.no");
				})
				.coloured(true)
			)
			.build();

		distanceFromWorldCenterOptions.put(ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME, enableMaxDistanceFromWorldCenterOption);

		var maxStructureDistanceFromWorldOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.max_distance_from_world_center.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.max_distance_from_world_center.description")))
			.available(distanceFromWorldCenterData.isMaxDistanceFromWorldCenterEnabled())
			.binding(
				DistanceFromWorldCenterCheckData.MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE,
				distanceFromWorldCenterData::getMaxDistanceFromWorldCenter,
				distanceFromWorldCenterData::setMaxDistanceFromWorldCenter
			)
			.controller(opt -> IntegerFieldControllerBuilder.create(opt).range(DistanceFromWorldCenterCheckData.DISTANCE_FROM_WORLD_CENTER_MIN_LIMIT, DistanceFromWorldCenterCheckData.DISTANCE_FROM_WORLD_CENTER_MAX_LIMIT)).build();

		distanceFromWorldCenterOptions.put(MAX_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME, maxStructureDistanceFromWorldOption);

		var minDistanceFromWorldCenterPair = new OptionPair<>(enableMinDistanceFromWorldCenterOption, minStructureDistanceFromWorldOption);

		var minDistanceFromWorldCenterOption =
			HolderOption.<Option<Boolean>, Option<Integer>>createBuilder()
				.optionPair(minDistanceFromWorldCenterPair)
				.controller(opt -> DualControllerBuilder.create(minDistanceFromWorldCenterPair))
				.build();

		builder.option(minDistanceFromWorldCenterOption);

		var maxDistanceFromWorldCenterPair = new OptionPair<>(enableMaxDistanceFromWorldCenterOption, maxStructureDistanceFromWorldOption);

		var maxDistanceFromWorldCenterOption =
			HolderOption.<Option<Boolean>, Option<Integer>>createBuilder()
				.optionPair(maxDistanceFromWorldCenterPair)
				.controller(opt -> DualControllerBuilder.create(maxDistanceFromWorldCenterPair))
				.build();

		builder.option(maxDistanceFromWorldCenterOption);

		enableMinDistanceFromWorldCenterOption.addListener((opt, currentIsEnabled) -> {
			if (!currentIsEnabled) {
				minStructureDistanceFromWorldOption.requestSetDefault();
			}

			minStructureDistanceFromWorldOption.setAvailable(currentIsEnabled);
			modeOption.setAvailable(currentIsEnabled || enableMaxDistanceFromWorldCenterOption.pendingValue());
		});

		enableMaxDistanceFromWorldCenterOption.addListener((opt, currentIsEnabled) -> {
			if (!currentIsEnabled) {
				maxStructureDistanceFromWorldOption.requestSetDefault();
			}

			maxStructureDistanceFromWorldOption.setAvailable(currentIsEnabled);
			modeOption.setAvailable(enableMinDistanceFromWorldCenterOption.pendingValue() || currentIsEnabled);
		});

		if (isOverridingGlobalDistanceFromWorldCenterOption != null) {
			isOverridingGlobalDistanceFromWorldCenterOption.addListener((opt, currentOverrideGlobalDistanceFromWorldCenter) -> {
				if (!currentOverrideGlobalDistanceFromWorldCenter) {
					enableMinDistanceFromWorldCenterOption.setAvailable(false);
					enableMinDistanceFromWorldCenterOption.requestSetDefault();

					enableMaxDistanceFromWorldCenterOption.setAvailable(false);
					enableMaxDistanceFromWorldCenterOption.requestSetDefault();

					modeOption.requestSetDefault();
				} else {
					enableMinDistanceFromWorldCenterOption.setAvailable(true);
					enableMaxDistanceFromWorldCenterOption.setAvailable(true);
				}
			});
		}

		return distanceFromWorldCenterOptions;
	}
}
