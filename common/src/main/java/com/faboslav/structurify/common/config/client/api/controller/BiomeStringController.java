package com.faboslav.structurify.common.config.client.api.controller;

import com.faboslav.structurify.common.config.client.api.controller.element.BiomeStringControllerElement;
import com.faboslav.structurify.common.config.data.WorldgenDataProvider;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.dropdown.AbstractDropdownController;
import net.minecraft.text.Text;

public final class BiomeStringController extends AbstractDropdownController<String>
{
	public BiomeStringController(Option<String> option) {
		super(option, WorldgenDataProvider.getBiomes(), false, false);
	}

	@Override
	public String getString() {
		return option.pendingValue().toString();
	}

	@Override
	public void setFromString(String value) {
		option.requestSet(value);
	}

	@Override
	public Text formatValue() {
		return LanguageUtil.translateId("biome", getString());
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new BiomeStringControllerElement(this, screen, widgetDimension);
	}
}