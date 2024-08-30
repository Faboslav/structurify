package com.faboslav.structurify.common.config.client.gui;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.client.gui.widget.DynamicGridWidget;
import com.faboslav.structurify.common.config.client.gui.widget.ImageButtonWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class StructurifyConfigScreen extends Screen
{
	private final Screen parent;
	@Nullable
	private Screen structuresScreen = null;
	@Nullable
	private Screen structureSetsScreen = null;

	public StructurifyConfigScreen(@Nullable Screen parent) {
		super(Component.translatable("structurify"));
		this.parent = parent;
	}

	@Override
	public void onClose() {
		assert this.minecraft != null;
		this.minecraft.setScreen(this.parent);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		/*? if <1.20.2 {*/
		super.renderBackground(context);
		/*?} else {*/
		/*super.renderBackground(context, mouseX, mouseY, delta);
		 *//*?}*/
		super.render(context, mouseX, mouseY, delta);

		assert this.minecraft != null;
		context.drawCenteredString(this.minecraft.font, Component.translatable("gui.structurify.title"), this.width / 2, 10, 0xFFFFFF);
	}

	@Override
	protected void init() {
		super.init();

		int fontHeight = this.font.lineHeight;
		DynamicGridWidget grid = new DynamicGridWidget(10, 10 + fontHeight + 10, width - 13, height - 20 - fontHeight - 10 - 20);

		grid.setPadding(3);

		grid.addChild(new ImageButtonWidget(0, 0, 0, 0, Component.translatable("gui.structurify.structures.title"), Structurify.makeId("textures/gui/config/images/buttons/structures.webp"), btn -> {
			if (this.structuresScreen == null) {
				this.structuresScreen = StructuresConfigScreen.createConfigGui(Structurify.getConfig(), this);
			}

			this.minecraft.setScreen(this.structuresScreen);
		}), 2, 1);

		grid.addChild(new ImageButtonWidget(0, 0, 0, 0, Component.translatable("gui.structurify.structure_sets.title"), Structurify.makeId("textures/gui/config/images/buttons/structure_sets.webp"), btn -> {
			if (this.structureSetsScreen == null) {
				this.structureSetsScreen = StructureSetsConfigScreen.createConfigGui(Structurify.getConfig(), this);
			}

			this.minecraft.setScreen(this.structureSetsScreen);
		}), 2, 1);

		grid.calculateLayout();
		grid.visitWidgets(this::addRenderableWidget);

		int discordAndKoFiButtonsWidth = 100 + 100 + 30; // button widths + left margin of Ko-Fi button + right margin of Discord button
		int doneButtonWidth = this.width - discordAndKoFiButtonsWidth;
		var buttonWidget = Button.builder(CommonComponents.GUI_DONE, (btn) -> this.minecraft.setScreen(this.parent)).bounds(this.width / 2 - doneButtonWidth / 2, this.height - 30, doneButtonWidth, 20).build();
		var donateButton = Button.builder(Component.literal("Donate").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD), (btn) -> Util.getPlatform().openUri("https://ko-fi.com/faboslav")).bounds(10, this.height - 30, 100, 20).build();
		var discordButton = Button.builder(Component.literal("Discord").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD), (btn) -> Util.getPlatform().openUri("https://discord.gg/QGwFvvMQCn")).bounds(this.width - 110, this.height - 30, 100, 20).build();

		this.addRenderableWidget(buttonWidget);
		this.addRenderableWidget(donateButton);
		this.addRenderableWidget(discordButton);
	}
}