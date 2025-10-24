package com.faboslav.structurify.common.config.client.gui.structure;

import com.faboslav.structurify.common.StructurifyClient;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.BiomeStringControllerBuilder;
import com.faboslav.structurify.common.config.data.StructureLikeData;
import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BiomeCheckOptions
{
	public static String OVERRIDE_GLOBAL_BIOME_CHECK_OPTION_NAME = "override_global_biome_check";
	public static String BIOME_CHECK_IS_ENABLED_OPTION_NAME = "is_enabled";
	public static String BIOME_CHECK_MODE_OPTION_NAME = "mode";
	public static String BIOME_CHECK_BLACKLISTED_BIOMES_OPTION_NAME = "blacklisted_biomes";

	public static Map<String, Option<?>> addBiomeCheckOptions(ConfigCategory.Builder categoryBuilder, OptionGroup.Builder groupBuilder, StructurifyConfig config, String id) {
		boolean isEnabledGlobally = config.getStructureNamespaceData().get(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER).getBiomeCheckData().isEnabled();
		boolean isGlobal = id.equals(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER);
		boolean isEnabledForNamespace = config.getStructureNamespaceData().get(id.split(":")[0]).getBiomeCheckData().isEnabled();
		boolean isNamespace = !id.contains(":");

		String namespace;
		Map<String, ? extends StructureLikeData> structureLikeData;

		if(isNamespace) {
			namespace = id;
			structureLikeData = config.getStructureNamespaceData();
		} else {
			namespace = id.split(":")[0];
			structureLikeData = config.getStructureData();
		}

		var biomeCheckOptions = new HashMap<String, Option<?>>();
		var biomeCheckData = structureLikeData.get(id).getBiomeCheckData();
		boolean isEnabled = structureLikeData.get(id).getBiomeCheckData().isEnabled();
		var title = Component.translatable("gui.structurify.structures.biome_check_group.title");

		if(isGlobal || isNamespace) {
			title = Component.literal("„" + LanguageUtil.translateId(null, namespace).getString() + "“ ").append(title);
		}

		title = Component.literal("\n").append(title);

		groupBuilder.option(LabelOption.create(title.withStyle(style -> style.withBold(true))));

		@Nullable Option<Boolean> isOverridingGlobalBiomeCheckOption;

		if(!isGlobal) {
			isOverridingGlobalBiomeCheckOption = Option.<Boolean>createBuilder()
				.name(Component.translatable("gui.structurify.structures.structure.override_global_biome_check.title"))
				.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.override_global_biome_check.description", namespace, id)))
				.available(isEnabledGlobally || isEnabledForNamespace)
				.binding(
					BiomeCheckData.OVERRIDE_GLOBAL_BIOME_CHECK_DEFAULT_VALUE,
					() -> structureLikeData.get(id).getBiomeCheckData().isOverridingGlobalBiomeCheck(),
					overrideGlobalBiomeCheck -> structureLikeData.get(id).getBiomeCheckData().overrideGlobalBiomeCheck(overrideGlobalBiomeCheck)
				).controller(opt -> BooleanControllerBuilder.create(opt)
					.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
					.coloured(true)
				)
				.build();

			biomeCheckOptions.put(OVERRIDE_GLOBAL_BIOME_CHECK_OPTION_NAME, isOverridingGlobalBiomeCheckOption);
			groupBuilder.option(isOverridingGlobalBiomeCheckOption);
		} else {
			isOverridingGlobalBiomeCheckOption = null;
		}

		var isEnabledOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.enable_biome_check.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.enable_biome_check.description")))
			.available(biomeCheckData.isOverridingGlobalBiomeCheck())
			.binding(
				BiomeCheckData.IS_ENABLED_DEFAULT_VALUE,
				biomeCheckData::isEnabled,
				biomeCheckData::enable
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
				.coloured(true)).build();

		biomeCheckOptions.put(BIOME_CHECK_IS_ENABLED_OPTION_NAME, isEnabledOption);
		groupBuilder.option(isEnabledOption);

		var modeOption = Option.<BiomeCheckData.BiomeCheckMode>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.biome_check_mode.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.biome_check_mode.description")))
			.available(biomeCheckData.isEnabled())
			.binding(
				BiomeCheckData.MODE_DEFAULT_VALUE,
				biomeCheckData::getMode,
				biomeCheckData::setMode
			).controller(opt -> EnumControllerBuilder.create(opt)
				.enumClass(BiomeCheckData.BiomeCheckMode.class)
				.valueFormatter(biomeCheckMode -> Component.translatable("gui.structurify.structures.structure.biome_check_mode." + biomeCheckMode.name().toLowerCase()))).build();

		biomeCheckOptions.put(BIOME_CHECK_MODE_OPTION_NAME, modeOption);
		groupBuilder.option(modeOption);

		var blacklistedBiomesOption = ListOption.<String>createBuilder()
			.name(Component.translatable("gui.structurify.structures.structure.biome_check_blacklisted_biomes.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structures.structure.biome_check_blacklisted_biomes.description")))
			.available(biomeCheckData.isEnabled())
			.collapsed(false)
			.insertEntriesAtEnd(false)
			.binding(
				BiomeCheckData.BLACKLISTED_BIOMES_DEFAULT_VALUE,
				() -> biomeCheckData.getBlacklistedBiomes(),
				blacklistedBiomes -> biomeCheckData.setBlacklistedBiomes(blacklistedBiomes)
			)
			.controller(BiomeStringControllerBuilder::create)
			.available(biomeCheckData.getMode() == BiomeCheckData.BiomeCheckMode.BLACKLIST)
			.initial("").build();

		// This is needed, since options inside list are not disabled by .available() setting
		blacklistedBiomesOption.setAvailable(biomeCheckData.isEnabled());

		biomeCheckOptions.put(BIOME_CHECK_BLACKLISTED_BIOMES_OPTION_NAME, blacklistedBiomesOption);

		if(isOverridingGlobalBiomeCheckOption != null) {
			isOverridingGlobalBiomeCheckOption.addListener((opt, currentOverrideGlobalBiomeCheck) -> {
				isEnabledOption.setAvailable(currentOverrideGlobalBiomeCheck);

				if(!currentOverrideGlobalBiomeCheck) {
					isEnabledOption.requestSetDefault();
				}

				var configScreen = StructurifyClient.getConfigScreen();
				if (configScreen == null || configScreen.structureScreens == null) {
					return;
				}

				configScreen.structureScreens.clear();
			});
		}

		isEnabledOption.addListener((opt, currentIsEnabled) -> {
			boolean isBlacklistAvailable = biomeCheckData.getMode() == BiomeCheckData.BiomeCheckMode.BLACKLIST;
			modeOption.setAvailable(currentIsEnabled);
			blacklistedBiomesOption.setAvailable(currentIsEnabled && isBlacklistAvailable);
		});

		modeOption.addListener((opt, biomeCheckMode) -> {
			boolean isBlacklistAvailable = biomeCheckMode == BiomeCheckData.BiomeCheckMode.BLACKLIST;
			blacklistedBiomesOption.setAvailable(isBlacklistAvailable);
		});

		return biomeCheckOptions;
	}
}