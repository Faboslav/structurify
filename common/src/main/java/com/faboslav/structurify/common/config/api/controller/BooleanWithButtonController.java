package com.faboslav.structurify.common.config.api.controller;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.gui.StructureConfigScreen;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TextScaledButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.function.Function;

public class BooleanWithButtonController extends BooleanController
{
	public BooleanWithButtonController(
		Option<Boolean> option,
		Function<Boolean, Text> valueFormatter,
		boolean coloured
	) {
		super(option, valueFormatter, coloured);
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		return new BooleanWithButtonControllerElement(this, screen, widgetDimension);
	}

	public static class BooleanWithButtonControllerElement extends BooleanControllerElement
	{
		private final TextScaledButtonWidget configurationButton;

		public BooleanWithButtonControllerElement(
			BooleanController control,
			YACLScreen screen,
			Dimension<Integer> dim
		) {
			super(control, screen, dim);

			this.setDimension(this.getDimension().expanded(-20, 0));
			this.configurationButton = new TextScaledButtonWidget(screen, this.getDimension().xLimit(), -50, 20, 20, 2.0f, Text.literal("\u2699"), button -> {
				var structureScreen = StructureConfigScreen.create(screen);
				this.client.setScreen(structureScreen);
			});
			this.configurationButton.active = true;
		}

		@Override
		public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
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

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
			return super.mouseScrolled(mouseX, mouseY, delta) || this.configurationButton.mouseScrolled(mouseX, mouseY, delta);
		}
	}
}