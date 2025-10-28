package com.faboslav.structurify.common.config.client.api.controller.element;

import com.faboslav.structurify.common.Structurify;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TextScaledButtonWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jetbrains.annotations.Nullable;

//? if >=1.21.9 {
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}

public final class DualControllerElement extends AbstractWidget
{
	private final AbstractWidget firstElement;
	private final AbstractWidget secondElement;
	@Nullable
	private final TextScaledButtonWidget resetButton;

	public DualControllerElement(
		Dimension<Integer> dim,
		AbstractWidget firstElement,
		AbstractWidget secondElement,
		@Nullable TextScaledButtonWidget resetButton
	) {
		super(dim);

		this.firstElement = firstElement;
		this.secondElement = secondElement;
		this.resetButton = resetButton;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		firstElement.mouseMoved(mouseX, mouseY);
		secondElement.mouseMoved(mouseX, mouseY);
		resetButton.mouseMoved(mouseX, mouseY);
	}

	//? if >=1.21.9 {
	@Override
	public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick) {
		firstElement.setFocused(false);
		secondElement.setFocused(false);

		if(firstElement.mouseClicked(mouseButtonEvent, doubleClick)) {
			firstElement.setFocused(true);
			return true;
		}

		if(secondElement.mouseClicked(mouseButtonEvent, doubleClick)) {
			secondElement.setFocused(true);
			return true;
		}

		return resetButton.mouseClicked(mouseButtonEvent, doubleClick);
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
		return firstElement.mouseReleased(mouseButtonEvent) || secondElement.mouseReleased(mouseButtonEvent) || resetButton.mouseReleased(mouseButtonEvent);
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double dx, double dy) {
		return firstElement.mouseDragged(mouseButtonEvent, dx, dy) || secondElement.mouseDragged(mouseButtonEvent, dx, dy) || resetButton.mouseDragged(mouseButtonEvent, dx, dy);
	}

	@Override
	public boolean keyPressed(KeyEvent keyEvent) {
		return firstElement.keyPressed(keyEvent) || secondElement.keyPressed(keyEvent) || resetButton.keyPressed(keyEvent);
	}

	@Override
	public boolean keyReleased(KeyEvent keyEvent) {
		return firstElement.keyReleased(keyEvent) || secondElement.keyReleased(keyEvent) || resetButton.keyReleased(keyEvent);
	}

	@Override
	public boolean charTyped(CharacterEvent characterEvent) {
		return firstElement.charTyped(characterEvent) || secondElement.charTyped(characterEvent) || secondElement.charTyped(characterEvent);
	}
	//?} else {
	/*@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		firstElement.setFocused(false);
		secondElement.setFocused(false);

		if(firstElement.mouseClicked(mouseX, mouseY, button)) {
			firstElement.setFocused(true);
			return true;
		}

		if(secondElement.mouseClicked(mouseX, mouseY, button)) {
			secondElement.setFocused(true);
			return true;
		}

		return resetButton.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return firstElement.mouseReleased(mouseX, mouseY, button) || secondElement.mouseReleased(mouseX, mouseY, button) || resetButton.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return firstElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || secondElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return firstElement.keyPressed(keyCode, scanCode, modifiers) || secondElement.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return firstElement.keyReleased(keyCode, scanCode, modifiers) || secondElement.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		return firstElement.charTyped(chr, modifiers) || secondElement.charTyped(chr, modifiers);
	}
	*///?}

	//? if >=1.20.4 {
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		return this.firstElement.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount) || this.secondElement.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}
	//?}

	@Override
	public void setFocused(boolean focused) {
	}

	@Override
	public boolean isFocused() {
		return false;
	}

	@Override
	public void setDimension(Dimension<Integer> dim) {
		Dimension<Integer> firstElementDimension = dim.moved(0, 0).withWidth(firstElement.getDimension().width()).withHeight(firstElement.getDimension().height());
		Dimension<Integer> secondElementDimension = dim.moved(firstElement.getDimension().width(), 0).withWidth(secondElement.getDimension().width()).withHeight(secondElement.getDimension().height());

		firstElement.setDimension(firstElementDimension);
		secondElement.setDimension(secondElementDimension);

		if (resetButton != null) {
			resetButton.setY(getDimension().y());
		}

		super.setDimension(dim);
	}

	@Override
	public void unfocus() {
		firstElement.unfocus();
		secondElement.unfocus();
		super.unfocus();
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float tickDelta) {
		firstElement.render(graphics, mouseX, mouseY, tickDelta);
		secondElement.render(graphics, mouseX, mouseY, tickDelta);

		if (resetButton != null) {
			resetButton.setY(getDimension().y());
			resetButton.render(graphics, mouseX, mouseY, tickDelta);
		}
	}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}

	@Override
	public boolean matchesSearch(String query) {
		boolean matchesSearchInFirstElement = firstElement.matchesSearch(query);
		boolean matchesSearchInSecondElement = firstElement.matchesSearch(query);

		return matchesSearchInFirstElement || matchesSearchInSecondElement;
	}

	@Override
	public void updateNarration(NarrationElementOutput builder) {
		firstElement.updateNarration(builder);
		secondElement.updateNarration(builder);
	}
}