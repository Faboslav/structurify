package com.faboslav.structurify.common.config.client.api;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TextScaledButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import org.jetbrains.annotations.Nullable;

public final class DualControllerElement extends AbstractWidget
{
	private final AbstractWidget labelElement;
	private final AbstractWidget firstElement;
	private final AbstractWidget secondElement;
	@Nullable
	private final TextScaledButtonWidget resetButton;

	public DualControllerElement(
		Dimension<Integer> dim,
		AbstractWidget labelElement,
		AbstractWidget firstElement,
		AbstractWidget secondElement,
		@Nullable TextScaledButtonWidget resetButton
	) {
		super(dim);

		this.labelElement = labelElement;
		this.firstElement = firstElement;
		this.secondElement = secondElement;
		this.resetButton = resetButton;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		labelElement.mouseMoved(mouseX, mouseY);
		firstElement.mouseMoved(mouseX, mouseY);
		secondElement.mouseMoved(mouseX, mouseY);
		resetButton.mouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean labelMouseClicked = labelElement.mouseClicked(mouseX, mouseY, button);
		boolean firstElementMouseClicked = firstElement.mouseClicked(mouseX, mouseY, button);
		boolean secondElementMouseClicked = secondElement.mouseClicked(mouseX, mouseY, button);
		boolean resetButtonMouseClicked = resetButton.mouseClicked(mouseX, mouseY, button);

		return labelMouseClicked || firstElementMouseClicked || secondElementMouseClicked || resetButtonMouseClicked;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		boolean labelMouseReleased = labelElement.mouseReleased(mouseX, mouseY, button);
		boolean firstElementMouseReleased = firstElement.mouseReleased(mouseX, mouseY, button);
		boolean secondElementMouseReleased = secondElement.mouseReleased(mouseX, mouseY, button);
		boolean resetButtonMouseReleased = resetButton.mouseReleased(mouseX, mouseY, button);

		return labelMouseReleased || firstElementMouseReleased || secondElementMouseReleased || resetButtonMouseReleased;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return firstElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) || secondElement.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

		/*? if =1.20.1 {*/
		/*@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
			return firstElement.mouseScrolled(mouseX, mouseY, delta) || secondElement.mouseScrolled(mouseX, mouseY, delta);
		}
		*//*?} else {*/
		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
			return firstElement.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount) || secondElement.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}
		/*?}*/

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

	@Override
	public void setFocused(boolean focused) {
		labelElement.setFocused(focused);
		firstElement.setFocused(focused);
		secondElement.setFocused(focused);
	}

	@Override
	public boolean isFocused() {
		return labelElement.isFocused() || firstElement.isFocused() || secondElement.isFocused();
	}

	@Override
	public void setDimension(Dimension<Integer> dim) {
		Dimension<Integer> labelElementDimension = dim.moved(0, 0).withWidth(labelElement.getDimension().width()).withHeight(labelElement.getDimension().height());
		Dimension<Integer> firstElementDimension = dim.moved(0, labelElement.getDimension().height()).withWidth(firstElement.getDimension().width()).withHeight(firstElement.getDimension().height());
		Dimension<Integer> secondElementDimension = dim.moved(firstElement.getDimension().width(), labelElement.getDimension().height()).withWidth(secondElement.getDimension().width()).withHeight(secondElement.getDimension().height());

		labelElement.setDimension(labelElementDimension);
		firstElement.setDimension(firstElementDimension);
		secondElement.setDimension(secondElementDimension);

		if (resetButton != null) {
			resetButton.setY(getDimension().y());
		}

		resetButton.setY(getDimension().y());

		super.setDimension(dim);
	}

	@Override
	public void unfocus() {
		labelElement.unfocus();
		firstElement.unfocus();
		secondElement.unfocus();
		super.unfocus();
	}

	@Override
	public void render(DrawContext graphics, int mouseX, int mouseY, float tickDelta) {
		labelElement.render(graphics, mouseX, mouseY, tickDelta);
		firstElement.render(graphics, mouseX, mouseY, tickDelta);
		secondElement.render(graphics, mouseX, mouseY, tickDelta);

		if (resetButton != null) {
			resetButton.setY(getDimension().y() + labelElement.getDimension().height());
			resetButton.render(graphics, mouseX, mouseY, tickDelta);
		}
	}

	@Override
	public SelectionType getType() {
		return SelectionType.NONE;
	}

	@Override
	public boolean matchesSearch(String query) {
		boolean matchesSearchInLabel = labelElement.matchesSearch(query);
		boolean matchesSearchInFirstElement = firstElement.matchesSearch(query);
		boolean matchesSearchInSecondElement = firstElement.matchesSearch(query);

		return matchesSearchInLabel || matchesSearchInFirstElement || matchesSearchInSecondElement;
	}

	@Override
	public void appendNarrations(NarrationMessageBuilder builder) {
		labelElement.appendNarrations(builder);
		firstElement.appendNarrations(builder);
		secondElement.appendNarrations(builder);
	}
}