package com.faboslav.structurify.common.config.client.gui.widget;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.gui.DescriptionWithName;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class CustomOptionListWidget extends OptionListWidget
{
	public CustomOptionListWidget(
		YACLScreen screen,
		ConfigCategory category,
		Minecraft client,
		int x,
		int y,
		int width,
		int height,
		Consumer<DescriptionWithName> hoverEvent
	) {
		super(screen, category, client, x, y, width, height, hoverEvent);
	}
}
