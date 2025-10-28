package com.faboslav.structurify.common.config.client.gui.structure;

import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.DualControllerBuilder;
import com.faboslav.structurify.common.config.client.api.option.HolderOption;
import com.faboslav.structurify.common.config.data.StructureLikeData;
import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import com.faboslav.structurify.common.config.data.structure.DistanceFromWorldCenterCheckData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionAddable;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DistanceFromWorldCenterOptions
{
	public static String OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME = "override_global_distance_from_world_center";
	public static String DISTANCE_FROM_WORLD_CENTER_OPTION_NAME = "distance_from_world_center";

	public static Map<String, Option<?>> addDistanceFromWorldCenterOptions(
		OptionAddable builder,
		StructurifyConfig config,
		String id
	) {
		boolean isEnabledGlobally = !config.getStructureNamespaceData().get(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER).getDistanceFromWorldCenterCheckData().isUsingDefaultValues();
		boolean isGlobal = id.equals(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER);
		boolean isEnabledForNamespace = !config.getStructureNamespaceData().get(id.split(":")[0]).getDistanceFromWorldCenterCheckData().isUsingDefaultValues();
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

		var title = Component.translatable("gui.structurify.structures.distance_from_world_center_group.title");

		if (isGlobal || isNamespace) {
			title = Component.literal("„" + LanguageUtil.translateId(null, namespace).getString() + "“ ").append(title);
		}

		title = Component.literal("\n").append(title);

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
					.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
					.coloured(true)
				)
				.build();

			distanceFromWorldCenterOptions.put(OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_OPTION_NAME, isOverridingGlobalDistanceFromWorldCenterOption);
			builder.option(isOverridingGlobalDistanceFromWorldCenterOption);
		} else {
			isOverridingGlobalDistanceFromWorldCenterOption = null;
		}

		var minStructureDistanceFromWorldOption = Option.<Integer>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.min_distance_from_world_center.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.min_distance_from_world_center.description")))
			.available(isGlobal || distanceFromWorldCenterData.isOverridingGlobalDistanceFromWorldCenter() || (!isEnabledGlobally && !isEnabledForNamespace))
			.binding(
				DistanceFromWorldCenterCheckData.MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE,
				distanceFromWorldCenterData::getMinDistanceFromWorldCenter,
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

		var distanceFromWorldCenterOption = HolderOption.<Option<Integer>, Option<Integer>>createBuilder()
			.controller(opt -> DualControllerBuilder.create(minStructureDistanceFromWorldOption, maxStructureDistanceFromWorldOption)).build();
		distanceFromWorldCenterOptions.put(DISTANCE_FROM_WORLD_CENTER_OPTION_NAME, distanceFromWorldCenterOption);

		builder.option(distanceFromWorldCenterOption);

		if (isOverridingGlobalDistanceFromWorldCenterOption != null) {
			isOverridingGlobalDistanceFromWorldCenterOption.addListener((opt, currentOverrideGlobalFlatnessCheck) -> {
				minStructureDistanceFromWorldOption.setAvailable(currentOverrideGlobalFlatnessCheck);
				maxStructureDistanceFromWorldOption.setAvailable(currentOverrideGlobalFlatnessCheck);

				if (!currentOverrideGlobalFlatnessCheck) {
					minStructureDistanceFromWorldOption.requestSetDefault();
					maxStructureDistanceFromWorldOption.requestSetDefault();
				}
			});
		}

		return distanceFromWorldCenterOptions;
	}
}
