package com.faboslav.structurify.common.config.client.gui.structure;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.StructurifyClient;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.data.StructureLikeData;
import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class FlatnessCheckOptions
{
	public static String OVERRIDE_GLOBAL_FLATNESS_CHECK_OPTION_NAME = "override_global_flatness_check";
	public static String FLATNESS_CHECK_IS_ENABLED_OPTION_NAME = "is_enabled";
	public static String FLATNESS_CHECK_ALLOW_NON_SOLID_BLOCKS_OPTION_NAME = "allow_non_solid_blocks";

	public static Map<String, Option<?>> addFlatnessCheckOptions(OptionAddable builder, StructurifyConfig config, String id) {
		boolean isEnabledGlobally = config.getStructureNamespaceData().get(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER).getFlatnessCheckData().isEnabled();
		boolean isGlobal = id.equals(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER);
		boolean isNamespace = !id.contains(":");
		String namespace;
		boolean isEnabledForNamespace = config.getStructureNamespaceData().get(id.split(":")[0]).getFlatnessCheckData().isEnabled();
		Map<String, ? extends StructureLikeData> structureLikeData;
		var flatnessCheckOptions = new HashMap<String, Option<?>>();

		if(isNamespace) {
			namespace = id;
			structureLikeData = config.getStructureNamespaceData();
		} else {
			namespace = id.split(":")[0];
			structureLikeData = config.getStructureData();
		}

		var flatnessCheckData = structureLikeData.get(id).getFlatnessCheckData();
		boolean isEnabled = structureLikeData.get(id).getFlatnessCheckData().isEnabled();
		var title = Component.translatable("gui.structurify.structures.flatness_check_group.title");

		if(isGlobal || isNamespace) {
			title = Component.literal("„" + LanguageUtil.translateId(null, namespace).getString() + "“ ").append(title);
		}

		title = Component.literal("\n").append(title);

		builder.option(LabelOption.create(title.withStyle(style -> style.withBold(true))));

		@Nullable Option<Boolean> isOverridingGlobalFlatnessCheckOption;

		if(!isGlobal) {
			isOverridingGlobalFlatnessCheckOption = Option.<Boolean>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.override_global_flatness_check.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.override_global_flatness_check.description", namespace, id)))
				.available(isEnabledGlobally || isEnabledForNamespace)
				.binding(
					FlatnessCheckData.OVERRIDE_GLOBAL_FLATNESS_CHECK_DEFAULT_VALUE,
					() -> structureLikeData.get(id).getFlatnessCheckData().isOverridingGlobalFlatnessCheck(),
					overrideGlobalFlatnessCheck -> structureLikeData.get(id).getFlatnessCheckData().overrideGlobalFlatnessCheck(overrideGlobalFlatnessCheck)
				).controller(opt -> BooleanControllerBuilder.create(opt)
					.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
					.coloured(true)
				)
				.build();

			flatnessCheckOptions.put(OVERRIDE_GLOBAL_FLATNESS_CHECK_OPTION_NAME, isOverridingGlobalFlatnessCheckOption);
			builder.option(isOverridingGlobalFlatnessCheckOption);
		} else {
			isOverridingGlobalFlatnessCheckOption = null;
		}

		var isEnabledOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.enable_flatness_check.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.enable_flatness_check.description")))
			.available(isGlobal || flatnessCheckData.isOverridingGlobalFlatnessCheck() || (!isEnabledGlobally && !isEnabledForNamespace))
			.binding(
				FlatnessCheckData.IS_ENABLED_DEFAULT_VALUE,
				() -> structureLikeData.get(id).getFlatnessCheckData().isEnabled(),
				enableFlatnessCheck -> structureLikeData.get(id).getFlatnessCheckData().enable(enableFlatnessCheck)
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(currentIsEnabled -> {
					if (currentIsEnabled) {
						if(isGlobal) {
							return Component.translatable("gui.structurify.label.yes_global");
						}

						if(isNamespace) {
							return Component.translatable("gui.structurify.label.yes_namespace", namespace);
						}

						return Component.translatable("gui.structurify.label.yes");
					}

					/*
					if(
						(isOverridingGlobalFlatnessCheckOption != null && !isOverridingGlobalFlatnessCheckOption.pendingValue())
						&& isEnabledGlobally || isEnabledForNamespace) {
						return Component.translatable("gui.structurify.label.yes");
					}*/

					return Component.translatable("gui.structurify.label.no");
				})
				.coloured(true)
			)
			.build();

		flatnessCheckOptions.put(FLATNESS_CHECK_IS_ENABLED_OPTION_NAME, isEnabledOption);
		builder.option(isEnabledOption);

		var allowNonSolidBlocksOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.allow_non_solid_blocks_in_flatness_check.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.allow_non_solid_blocks_in_flatness_check.description")))
			.available(isEnabled)
			.binding(
				FlatnessCheckData.ALLOW_NON_SOLID_BLOCKS_DEFAULT_VALUE,
				() -> structureLikeData.get(id).getFlatnessCheckData().areNonSolidBlocksAllowed(),
				allowNonSolidBlocks -> structureLikeData.get(id).getFlatnessCheckData().allowNonSolidBlocks(allowNonSolidBlocks)
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(allowNonSolidBlocks -> {
					if(allowNonSolidBlocks) {
						return Component.translatable("gui.structurify.label.yes");
					}

					return Component.translatable("gui.structurify.label.no");
				})
				.coloured(true)).build();

		flatnessCheckOptions.put(FLATNESS_CHECK_ALLOW_NON_SOLID_BLOCKS_OPTION_NAME, allowNonSolidBlocksOption);
		builder.option(allowNonSolidBlocksOption);

		if(isOverridingGlobalFlatnessCheckOption != null) {
			isOverridingGlobalFlatnessCheckOption.addListener((opt, currentOverrideGlobalFlatnessCheck) -> {
				if(!currentOverrideGlobalFlatnessCheck) {
					isEnabledOption.setAvailable(false);
					isEnabledOption.requestSetDefault();

					allowNonSolidBlocksOption.requestSetDefault();
				} else {
					isEnabledOption.setAvailable(true);
				}

				var configScreen = StructurifyClient.getConfigScreen();
				if (configScreen == null || configScreen.structureScreens == null) {
					return;
				}

				configScreen.structureScreens.clear();
			});
		}

		isEnabledOption.addListener((opt, currentIsEnabled) -> {
			if(!currentIsEnabled) {
				allowNonSolidBlocksOption.requestSetDefault();
			}

			allowNonSolidBlocksOption.setAvailable(currentIsEnabled);

			var configScreen = StructurifyClient.getConfigScreen();
			if (configScreen == null || configScreen.structureScreens == null) {
				return;
			}

			configScreen.structureScreens.clear();
		});

		return flatnessCheckOptions;
	}
}
