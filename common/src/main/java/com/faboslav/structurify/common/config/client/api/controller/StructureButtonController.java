package com.faboslav.structurify.common.config.client.api.controller;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.StructurifyClient;
import com.faboslav.structurify.common.config.client.gui.StructureConfigScreen;
import com.faboslav.structurify.common.util.LanguageUtil;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TextScaledButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

//? if >= 1.21.9 {
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}

public class StructureButtonController extends BooleanController
{
	private final String structureId;

	public StructureButtonController(
		Option<Boolean> option,
		String structureId,
		Function<Boolean, Component> valueFormatter,
		boolean coloured
	) {
		super(option, valueFormatter, coloured);
		this.structureId = structureId;
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new BooleanWithConfigButtonWidget(this, screen, widgetDimension, structureId);
	}

	public static final class BooleanWithConfigButtonWidget extends AbstractWidget
	{
		private static final int CONFIG_BUTTON_WIDTH = 20;
		private static final int CONFIG_BUTTON_HEIGHT = 20;

		private final YACLScreen screen;
		private final String structureId;

		private final BooleanControllerElement booleanElement;
		private final TextScaledButtonWidget configurationButton;

		private boolean focused;

		public BooleanWithConfigButtonWidget(
			BooleanController controller,
			YACLScreen screen,
			Dimension<Integer> dim,
			String structureId
		) {
			super(dim);

			this.screen = screen;
			this.structureId = structureId;

			this.booleanElement = new BooleanControllerElement(controller, screen, dim);

			this.configurationButton = new TextScaledButtonWidget(
				screen,
				0,
				0,
				CONFIG_BUTTON_WIDTH,
				CONFIG_BUTTON_HEIGHT,
				1.0f,
				Component.literal("\u2699").withStyle(style -> style.withBold(true)),
				button -> openStructureConfig()
			);

			this.configurationButton.setTooltip(Tooltip.create(Component.translatable("gui.structurify.structures.structure.detail_button.tooltip", LanguageUtil.translateId("structure", structureId))));

			this.setDimension(dim);
		}

		private void openStructureConfig() {
			var configScreen = StructurifyClient.getConfigScreen();
			if (configScreen == null) {
				return;
			}

			screen.finishOrSave();

			YACLScreen structureScreen = StructureConfigScreen.create(Structurify.getConfig(), structureId, screen);

			configScreen.saveScreenState(screen);
			this.client.setScreen(structureScreen);
			configScreen.loadScreenState(structureScreen);
		}
		@Override
		public void setDimension(Dimension<Integer> dim) {
			super.setDimension(dim);

			int booleanWidth = Math.max(0, dim.width() - CONFIG_BUTTON_WIDTH);

			Dimension<Integer> booleanDim = dim.withWidth(booleanWidth);
			this.booleanElement.setDimension(booleanDim);

			int gearX = dim.xLimit() - CONFIG_BUTTON_WIDTH;
			int gearY = dim.y();

			this.configurationButton.setX(gearX);
			this.configurationButton.setY(gearY);
		}

		@Override
		public void render(GuiGraphics graphics, int mouseX, int mouseY, float tickDelta) {
			this.booleanElement.render(graphics, mouseX, mouseY, tickDelta);

			this.configurationButton.active = this.booleanElement.isActive();
			this.configurationButton.setY(getDimension().y());
			this.configurationButton.render(graphics, mouseX, mouseY, tickDelta);
		}

		@Override
		public boolean canReset() {
			return true;
		}

		@Override
		public void mouseMoved(double mouseX, double mouseY) {
			this.configurationButton.mouseMoved(mouseX, mouseY);
			this.booleanElement.mouseMoved(mouseX, mouseY);
		}

		@Override
		public void unfocus() {
			this.focused = false;
			this.booleanElement.unfocus();
			this.configurationButton.setFocused(false);
			super.unfocus();
		}

		@Override
		public void setFocused(boolean focused) {
			this.focused = focused;

			if (!focused) {
				this.booleanElement.setFocused(false);
				this.configurationButton.setFocused(false);
				return;
			}

			this.booleanElement.setFocused(true);
			this.configurationButton.setFocused(false);
		}

		@Override
		public boolean isFocused() {
			return this.focused || this.booleanElement.isFocused() || this.configurationButton.isFocused();
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			if (this.configurationButton.isMouseOver(mouseX, mouseY)) {
				return true;
			}

			return this.booleanElement.isMouseOver(mouseX, mouseY);
		}

		//? if >= 1.21.9 {
		@Override
		public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
			this.configurationButton.active = this.booleanElement.isActive();

			boolean overConfig = this.configurationButton.isMouseOver(mouseButtonEvent.x(), mouseButtonEvent.y());
			if (overConfig) {
				this.booleanElement.setFocused(false);
				this.configurationButton.setFocused(true);
				this.focused = true;

				if (this.configurationButton.mouseClicked(mouseButtonEvent, doubleClick)) {
					return true;
				}
			}

			this.configurationButton.setFocused(false);
			this.booleanElement.setFocused(true);
			this.focused = true;

			return this.booleanElement.mouseClicked(mouseButtonEvent, doubleClick);
		}

		@Override
		public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
			if (this.configurationButton.mouseReleased(mouseButtonEvent)) {
				return true;
			}

			return this.booleanElement.mouseReleased(mouseButtonEvent);
		}

		@Override
		public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dx, double dy) {
			if (this.configurationButton.mouseDragged(mouseButtonEvent, dx, dy)) {
				return true;
			}

			return this.booleanElement.mouseDragged(mouseButtonEvent, dx, dy);
		}

		@Override
		public boolean keyPressed(KeyEvent keyEvent) {
			if (this.configurationButton.isFocused() && this.configurationButton.keyPressed(keyEvent)) {
				return true;
			}

			return this.booleanElement.keyPressed(keyEvent);
		}

		@Override
		public boolean keyReleased(KeyEvent keyEvent) {
			if (this.configurationButton.isFocused() && this.configurationButton.keyReleased(keyEvent)) {
				return true;
			}

			return this.booleanElement.keyReleased(keyEvent);
		}

		@Override
		public boolean charTyped(CharacterEvent characterEvent) {
			if (this.configurationButton.isFocused() && this.configurationButton.charTyped(characterEvent)) {
				return true;
			}

			return this.booleanElement.charTyped(characterEvent);
		}
		//?} else {
		/*@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			this.configurationButton.active = this.booleanElement.isAvailable();

			boolean overConfig = this.configurationButton.isMouseOver(mouseX, mouseY);
			if (overConfig) {
				this.booleanElement.setFocused(false);
				this.configurationButton.setFocused(true);
				this.focused = true;

				if (this.configurationButton.mouseClicked(mouseX, mouseY, button)) {
					return true;
				}
			}

			this.configurationButton.setFocused(false);
			this.booleanElement.setFocused(true);
			this.focused = true;

			return this.booleanElement.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			if (this.configurationButton.mouseReleased(mouseX, mouseY, button)) {
				return true;
			}

			return this.booleanElement.mouseReleased(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			if (this.configurationButton.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
				return true;
			}

			return this.booleanElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (this.configurationButton.isFocused() && this.configurationButton.keyPressed(keyCode, scanCode, modifiers)) {
				return true;
			}

			return this.booleanElement.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
			if (this.configurationButton.isFocused() && this.configurationButton.keyReleased(keyCode, scanCode, modifiers)) {
				return true;
			}

			return this.booleanElement.keyReleased(keyCode, scanCode, modifiers);
		}

		@Override
		public boolean charTyped(char chr, int modifiers) {
			if (this.configurationButton.isFocused() && this.configurationButton.charTyped(chr, modifiers)) {
				return true;
			}

			return this.booleanElement.charTyped(chr, modifiers);
		}
		*///?}

		//? if >= 1.20.4 {
		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
			if (this.configurationButton.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
				return true;
			}

			return this.booleanElement.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}
		//?}
	}
}