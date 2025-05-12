package com.faboslav.structurify.common.config.client.api.controller;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.StructurifyClient;
import com.faboslav.structurify.common.config.client.gui.StructureConfigScreen;
import com.faboslav.structurify.common.mixin.yacl.CategoryTabAccessor;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TextScaledButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

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
		return new BooleanWithButtonControllerElement(this, screen, widgetDimension, structureId);
	}

	public static class BooleanWithButtonControllerElement extends BooleanControllerElement
	{
		private final TextScaledButtonWidget configurationButton;

		public BooleanWithButtonControllerElement(
			BooleanController control,
			YACLScreen screen,
			Dimension<Integer> dim,
			String structureId
		) {
			super(control, screen, dim);

			this.setDimension(this.getDimension().expanded(-20, 0));
			this.configurationButton = new TextScaledButtonWidget(screen, this.getDimension().xLimit(), -50, 20, 20, 1.0f, Component.literal("\u2699").withStyle(style -> style.withBold(true)), button -> {
				var configScreen = StructurifyClient.getConfigScreen();

				if(configScreen == null) {
					return;
				}

				YACLScreen structureScreen;

				if(!configScreen.structureScreens.containsKey(structureId)) {
					structureScreen = StructureConfigScreen.create(screen, structureId);
					configScreen.structureScreens.put(structureId, structureScreen);
				} else {
					structureScreen = configScreen.structureScreens.get(structureId);
				}

				configScreen.saveScreenState(screen);
				this.client.setScreen(structureScreen);
				configScreen.loadScreenState(structureScreen);
			});
			this.configurationButton.active = true;
		}

		@Override
		public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
			this.configurationButton.setY(getDimension().y());
			this.configurationButton.render(graphics, mouseX, mouseY, delta);
			super.render(graphics, mouseX, mouseY, delta);
		}

		@Override
		public void mouseMoved(double mouseX, double mouseY) {
			super.mouseMoved(mouseX, mouseY);
			this.configurationButton.mouseMoved(mouseX, mouseY);
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return super.mouseClicked(mouseX, mouseY, button) || this.configurationButton.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return super.mouseReleased(mouseX, mouseY, button) || this.configurationButton.mouseReleased(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || this.configurationButton.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}

		/*? >=1.20.4 {*/
		/*@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
			return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount) || this.configurationButton.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}
		*//*?}*/
	}
}