package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.client.api.controller.builder.DualControllerBuilder;
import com.faboslav.structurify.common.config.client.api.option.HolderOption;
import com.faboslav.structurify.common.config.client.api.option.OptionPair;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class StructureSetsConfigScreen
{
	@Nullable
	public YACLScreen structureSetsScreen = null;

	public Map<String, AbstractMap.SimpleEntry<Option<Boolean>, Option<OptionPair<Option<Integer>, Option<Integer>>>>> structureSetOptions = new HashMap<>();
	public Map<String, OptionGroup> optionGroups = new HashMap<>();
	public Option<Boolean> enableGlobalSpacingAndSeparationOption = null;

	public void createStructureSetsScreen(StructurifyConfig config, Screen parent) {
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());

		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Component.translatable("gui.structurify.structure_sets_category.title"))
			.save(config::save);

		createStructureSetsTab(yacl, config);

		this.structureSetsScreen = (YACLScreen) yacl.build().generateScreen(parent);
	}

	@Nullable
	public YACLScreen getStructureSetsScreen() {
		return structureSetsScreen;
	}

	public void createStructureSetsTab(YetAnotherConfigLib.Builder yacl, StructurifyConfig config) {
		var structureSetCategoryBuilder = ConfigCategory.createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets_category.title"))
			.tooltip(Component.translatable("gui.structurify.structure_sets_category.description").append("\n\n").append(Component.translatable("gui.structurify.structure_sets.spacing.description")).append("\n\n").append(Component.translatable("gui.structurify.structure_sets.separation.description")));

		this.addGlobalOptions(structureSetCategoryBuilder, config);

		var structureSets = WorldgenDataProvider.getStructureSets();

		List<OptionGroup> optionGroups = new ArrayList<>();
		OptionGroup.Builder currentGroupBuilder = null;
		String currentNamespace = null;


		for (Map.Entry<String, StructureSetData> entry : structureSets.entrySet()) {
			String structureSetStringId = entry.getKey();
			ResourceLocation structureSetId = Structurify.makeNamespacedId(structureSetStringId);
			String namespace = structureSetId.getNamespace();

			// Create new group for each namespace
			if (!namespace.equals(currentNamespace)) {
				if (currentGroupBuilder != null) {
					optionGroups.add(currentGroupBuilder.build());
				}

				// Create new group
				currentGroupBuilder = OptionGroup.createBuilder()
					.name(Component.translatable("gui.structurify.structure_sets.structure_group.title", LanguageUtil.translateId(null, namespace)))
					.description(OptionDescription.of(Component.translatable("gui.structurify.structure_sets.structure_group.description", namespace)));
				currentNamespace = namespace;
			}

			var translatedStructureSetName = LanguageUtil.translateId("structure", structureSetStringId);

			var saltDescriptionBuilder = OptionDescription.createBuilder();
			saltDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.salt.description"));

			var saltOption = Option.<Integer>createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.salt.title"))
				.description(saltDescriptionBuilder.build())
				.binding(
					config.getStructureSetData().get(structureSetStringId).getDefaultSalt(),
					() -> config.getStructureSetData().get(structureSetStringId).getSalt(),
					salt -> config.getStructureSetData().get(structureSetStringId).setSalt(salt)
				)
				.controller(opt -> IntegerFieldControllerBuilder.create(opt).range(StructureSetData.MIN_SALT, StructureSetData.MAX_SALT).formatValue((value) -> Component.literal(value.toString()))).build();

			var frequencyDescriptionBuilder = OptionDescription.createBuilder();
			saltDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.frequency.description"));

			var frequencyOption = Option.<Float>createBuilder()
				.name(Component.translatable("gui.structurify.structure_sets.frequency.title"))
				.description(frequencyDescriptionBuilder.build())
				.binding(
					config.getStructureSetData().get(structureSetStringId).getDefaultFrequency(),
					() -> config.getStructureSetData().get(structureSetStringId).getFrequency(),
					frequency -> config.getStructureSetData().get(structureSetStringId).setFrequency(frequency)
				)
				.controller(opt -> FloatSliderControllerBuilder.create(opt).range(StructureSetData.MIN_FREQUENCY, StructureSetData.MAX_FREQUENCY).step(0.01F)).build();

			currentGroupBuilder.option(LabelOption.createBuilder().line(translatedStructureSetName).build());
			currentGroupBuilder.option(saltOption);
			currentGroupBuilder.option(frequencyOption);

			var defaultSpacing = config.getStructureSetData().get(structureSetStringId).getDefaultSpacing();
			var defaultSeparation = config.getStructureSetData().get(structureSetStringId).getDefaultSeparation();

			if (defaultSpacing != 0 && defaultSeparation != 0) {
				var overrideGlobalSpacingAndSeparationModifierDescriptionBuilder = OptionDescription.createBuilder();
				overrideGlobalSpacingAndSeparationModifierDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.override_global_spacing_and_separation_modifier.description", translatedStructureSetName));

				var overrideGlobalSpacingAndSeparationModifierOption = Option.<Boolean>createBuilder()
					.name(Component.translatable("gui.structurify.structure_sets.override_global_spacing_and_separation_modifier.title"))
					.description(overrideGlobalSpacingAndSeparationModifierDescriptionBuilder.build())
					.binding(
						StructureSetData.OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE,
						() -> config.getStructureSetData().get(structureSetStringId).overrideGlobalSpacingAndSeparationModifier(),
						overrideGlobalSpacingAndSeparationModifier -> config.getStructureSetData().get(structureSetStringId).setOverrideGlobalSpacingAndSeparationModifier(overrideGlobalSpacingAndSeparationModifier)
					)
					.controller(opt -> BooleanControllerBuilder.create(opt)
						.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
						.coloured(true)
					).available(config.enableGlobalSpacingAndSeparationModifier)
					.build();

				overrideGlobalSpacingAndSeparationModifierOption.addListener((opt, enableGlobalSpacingAndSeparationModifier) -> {
					boolean available = false;

					if (
						this.enableGlobalSpacingAndSeparationOption.pendingValue()
					) {
						available = true;
					}

					var structureSetOption = this.structureSetOptions.get(structureSetStringId);
					structureSetOption.getValue().setAvailable(available);
				});

				var spacingDescriptionBuilder = OptionDescription.createBuilder();
				spacingDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.spacing.description"));

				var spacingOption = Option.<Integer>createBuilder()
					.name(Component.translatable("gui.structurify.structure_sets.spacing.title"))
					.description(spacingDescriptionBuilder.build())
					.binding(
						config.getStructureSetData().get(structureSetStringId).getDefaultSpacing(),
						() -> config.getStructureSetData().get(structureSetStringId).getSpacing(),
						spacing -> config.getStructureSetData().get(structureSetStringId).setSpacing(spacing)
					)
					.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, StructureSetData.MAX_SPACING).step(1)).build();

				var separationDescriptionBuilder = OptionDescription.createBuilder();
				separationDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.separation.description"));
				separationDescriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structure_sets.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

				var separationOption = Option.<Integer>createBuilder()
					.name(Component.translatable("gui.structurify.structure_sets.separation.title"))
					.description(separationDescriptionBuilder.build())
					.binding(
						config.getStructureSetData().get(structureSetStringId).getDefaultSeparation(),
						() -> config.getStructureSetData().get(structureSetStringId).getSeparation(),
						separation -> config.getStructureSetData().get(structureSetStringId).setSeparation(separation)
					)
					.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, StructureSetData.MAX_SEPARATION).step(1)).build();

				spacingOption.addListener((opt, spacing) -> {
					if (spacing <= separationOption.pendingValue()) {
						spacingOption.requestSet(separationOption.pendingValue() + 1);
					}
				});

				separationOption.addListener((opt, separation) -> {
					if (separation >= spacingOption.pendingValue()) {
						separationOption.requestSet(spacingOption.pendingValue() - 1);
					}
				});
				var spacingAndSeparationOption = HolderOption.<Option<Integer>, Option<Integer>>createBuilder()
					.controller(opt -> DualControllerBuilder.create(LabelOption.createBuilder().line(translatedStructureSetName).build(), spacingOption, separationOption))
					.available(!config.enableGlobalSpacingAndSeparationModifier || config.getStructureSetData().get(structureSetStringId).overrideGlobalSpacingAndSeparationModifier()).build();

				currentGroupBuilder.option(overrideGlobalSpacingAndSeparationModifierOption);
				currentGroupBuilder.option(spacingAndSeparationOption);

				this.structureSetOptions.put(structureSetStringId, new AbstractMap.SimpleEntry<>(overrideGlobalSpacingAndSeparationModifierOption, spacingAndSeparationOption));
			}

			//currentGroupBuilder.collapsed(true);
		}

		if (currentGroupBuilder != null) {
			OptionGroup buildedGroup = currentGroupBuilder.build();
			//currentGroupBuilder.collapsed(true);
			this.optionGroups.put(buildedGroup.name().getString(), buildedGroup);
			optionGroups.add(buildedGroup);
		}

		for (OptionGroup structureOptionGroup : optionGroups) {
			structureSetCategoryBuilder.group(structureOptionGroup);
		}

		yacl.category(structureSetCategoryBuilder.build());
	}

	public void addGlobalOptions(ConfigCategory.Builder categoryBuilder, StructurifyConfig config) {
		var generalStructuresSetsGroupBuilder = OptionGroup.createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation.title"))
			.description(OptionDescription.of(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation.description")));

		var enableGlobalSpacingAndSeparationDescriptionBuilder = OptionDescription.createBuilder();
		enableGlobalSpacingAndSeparationDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.enable_global_spacing_and_separation_modifier.description"));
		enableGlobalSpacingAndSeparationDescriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structure_sets.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

		this.enableGlobalSpacingAndSeparationOption = Option.<Boolean>createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets.enable_global_spacing_and_separation_modifier.title"))
			.description(enableGlobalSpacingAndSeparationDescriptionBuilder.build())
			.binding(
				StructurifyConfig.ENABLE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE,
				() -> config.enableGlobalSpacingAndSeparationModifier,
				enableGlobalSpacingAndSeparationModifier -> config.enableGlobalSpacingAndSeparationModifier = enableGlobalSpacingAndSeparationModifier
			)
			.controller(opt -> BooleanControllerBuilder.create(opt)
				.valueFormatter(val -> val ? Component.translatable("gui.structurify.label.yes"):Component.translatable("gui.structurify.label.no"))
				.coloured(true)).build();

		generalStructuresSetsGroupBuilder.option(enableGlobalSpacingAndSeparationOption);

		enableGlobalSpacingAndSeparationOption.addListener((opt, enableGlobalSpacingAndSeparationModifier) -> {
			for (var structureSetOption : this.structureSetOptions.entrySet()) {
				var structureSetData = config.getStructureSetData().get(structureSetOption.getKey());
				var structureSetOptionOverride = structureSetOption.getValue().getKey();
				var structureSetOptionPair = structureSetOption.getValue().getValue();

				structureSetOptionOverride.setAvailable(enableGlobalSpacingAndSeparationModifier);
				structureSetOptionPair.setAvailable(!enableGlobalSpacingAndSeparationModifier || structureSetOptionOverride.pendingValue());

				/*
				if(!structureSetOptionOverride.available()) {
					var spacingOption = structureSetOptionPair.pendingValue().getFirstOption();
					var separationOption = structureSetOptionPair.pendingValue().getSecondOption();
					spacingOption.requestSet(RandomSpreadUtil.getModifiedSpacing(structureSetData, spacingOption.pendingValue()));
					separationOption.requestSet(RandomSpreadUtil.getModifiedSeparation(structureSetData, spacingOption.pendingValue(), separationOption.pendingValue()));
				}*/
			}
		});

		var globalSpacingAndSeparationModifierDescriptionBuilder = OptionDescription.createBuilder();
		globalSpacingAndSeparationModifierDescriptionBuilder.text(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation_modifier.description"));
		globalSpacingAndSeparationModifierDescriptionBuilder.text(Component.literal("\n\n").append(Component.translatable("gui.structurify.structure_sets.warning")).withStyle(style -> style.withColor(ChatFormatting.YELLOW)));

		var globalSpacingAndSeparationModifierOption = Option.<Double>createBuilder()
			.name(Component.translatable("gui.structurify.structure_sets.global_spacing_and_separation_modifier.title"))
			.description(globalSpacingAndSeparationModifierDescriptionBuilder.build())
			.binding(
				StructurifyConfig.GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE,
				() -> config.globalSpacingAndSeparationModifier,
				modifier -> config.globalSpacingAndSeparationModifier = modifier
			)
			.controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.1D, 100.0D).step(0.1D)).build();

		generalStructuresSetsGroupBuilder.option(globalSpacingAndSeparationModifierOption);

		categoryBuilder.group(generalStructuresSetsGroupBuilder.build());
	}
}