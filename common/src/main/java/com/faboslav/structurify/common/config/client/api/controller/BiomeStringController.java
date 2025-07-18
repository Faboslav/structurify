package com.faboslav.structurify.common.config.client.api.controller;

import com.faboslav.structurify.common.config.client.api.controller.element.BiomeStringControllerElement;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class BiomeStringController extends AbstractDropdownController<String>
{
	private final List<String> allowedValues;

	public BiomeStringController(Option<String> option) {
		super(option, WorldgenDataProvider.getBiomes(), false, false);
		this.allowedValues = WorldgenDataProvider.getBiomes();
	}

	@Override
	public String getString() {
		return option.pendingValue();
	}

	@Override
	public void setFromString(String value) {
		option.requestSet(value);
	}

	@Override
	protected String getValidValue(String value) {
		return this.getValidValue(value, 0);
	}

	@Override
	protected String getValidValue(String value, int offset) {
		if (value != null && !value.isBlank()) {
			var slugifiedValue = value.toLowerCase().replace(" ", "_");
			var validBiomes = this.allowedValues.stream().filter(biome -> {
				var slugifiedBiome = biome.toLowerCase().replace(" ", "_");
				var slugifiedTranslatedBiome = LanguageUtil.translateId("biome", biome).getString().toLowerCase().replace(" ", "_");
				return slugifiedBiome.contains(slugifiedValue) || slugifiedTranslatedBiome.contains(slugifiedValue);
			}).sorted((s1, s2) -> {

				if (s1.startsWith(slugifiedValue) && !s2.startsWith(slugifiedValue)) {
					return -1;
				} else {
					return !s1.startsWith(slugifiedValue) && s2.startsWith(slugifiedValue) ? 1:s1.compareTo(s2);
				}
			});

			if (offset >= 0) {
				var validBiome = validBiomes.skip(offset).findFirst().orElse(null);

				if (validBiome != null) {
					return validBiome;
				}
			}
		}

		return super.getValidValue(value, offset);
	}

	@Override
	public Component formatValue() {
		return LanguageUtil.translateId("biome", getString());
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new BiomeStringControllerElement(this, screen, widgetDimension);
	}
}