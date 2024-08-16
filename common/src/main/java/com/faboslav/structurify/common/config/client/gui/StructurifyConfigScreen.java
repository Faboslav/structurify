package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.client.gui.widget.DynamicGridWidget;
import com.faboslav.structurify.common.config.client.gui.widget.ImageButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class StructurifyConfigScreen extends Screen
{
	private final Screen parent;
	private final Screen structuresScreen;
	private final Screen structureSetsScreen;

	public StructurifyConfigScreen(@Nullable Screen parent) {
		super(Text.translatable("structurify"));
		this.parent = parent;
		this.structuresScreen = StructuresConfigScreen.createConfigGui(Structurify.getConfig(), this);
		;
		this.structureSetsScreen = StructureSetsConfigScreen.createConfigGui(Structurify.getConfig(), this);
		;
	}

	@Override
	public void close() {
		assert this.client != null;
		this.client.setScreen(this.parent);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		/*? if <1.20.2 {*/
		super.renderBackground(context);
		/*?} else {*/
		/*super.renderBackground(context, mouseX, mouseY, delta);
		 *//*?}*/
		super.render(context, mouseX, mouseY, delta);

		assert this.client != null;
		context.drawCenteredTextWithShadow(this.client.textRenderer, Text.translatable("gui.structurify.title"), this.width / 2, 10, 0xFFFFFF);
	}

	@Override
	protected void init() {
		super.init();

		int fontHeight = textRenderer.fontHeight;
		DynamicGridWidget grid = new DynamicGridWidget(10, 10 + fontHeight + 10, width - 20, height - 20 - fontHeight - 10 - 20);

		grid.setPadding(3);

		grid.addChild(new ImageButtonWidget(0, 0, 0, 0, Text.translatable("gui.structurify.structures.title"), Structurify.makeId("textures/gui/config/images/buttons/structures.webp"), btn -> {
			this.client.setScreen(this.structuresScreen);
		}), 2, 1);

		grid.addChild(new ImageButtonWidget(0, 0, 0, 0, Text.translatable("gui.structurify.structure_sets.title"), Structurify.makeId("textures/gui/config/images/buttons/structure_sets.webp"), btn -> {
			this.client.setScreen(this.structureSetsScreen);
		}), 2, 1);

		grid.calculateLayout();
		grid.forEachChild(this::addDrawableChild);

		int discordAndKoFiButtonsWidth = 100 + 100 + 30; // button widths + left margin of Ko-Fi button + right margin of Discord button
		int doneButtonWidth = this.width - discordAndKoFiButtonsWidth;
		ButtonWidget buttonWidget = ButtonWidget.builder(ScreenTexts.DONE, (btn) -> this.client.setScreen(this.parent)).dimensions(this.width / 2 - doneButtonWidth / 2, this.height - 30, doneButtonWidth, 20).build();
		ButtonWidget donateButton = ButtonWidget.builder(Text.literal("Donate").formatted(Formatting.GOLD).formatted(Formatting.BOLD), (btn) -> Util.getOperatingSystem().open("https://ko-fi.com/faboslav")).dimensions(10, this.height - 30, 100, 20).build();
		ButtonWidget discordButton = ButtonWidget.builder(Text.literal("Discord").formatted(Formatting.AQUA).formatted(Formatting.BOLD), (btn) -> Util.getOperatingSystem().open("https://discord.gg/QGwFvvMQCn")).dimensions(this.width - 110, this.height - 30, 100, 20).build();

		this.addDrawableChild(buttonWidget);
		this.addDrawableChild(donateButton);
		this.addDrawableChild(discordButton);
	}
}