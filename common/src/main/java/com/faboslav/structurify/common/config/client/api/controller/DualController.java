package com.faboslav.structurify.common.config.client.api.controller;

import com.faboslav.structurify.common.config.client.api.controller.element.DualControllerElement;
import com.faboslav.structurify.common.config.client.api.option.OptionPair;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.TextScaledButtonWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.network.chat.Component;

public class DualController<K extends Option<?>, V extends Option<?>> implements Controller<OptionPair<K, V>>
{
	public final LabelOption labelOption;
	public final OptionPair<K, V> optionPair;

	public DualController(
		LabelOption labelOption,
		OptionPair<K, V> controllerPair
	) {
		this.labelOption = labelOption;
		this.optionPair = controllerPair;
	}

	@Override
	public Option<OptionPair<K, V>> option() {
		return null;
	}

	@Override
	public Component formatValue() {
		return optionPair.getFirstOption().controller().formatValue().copy().append(" | ").append(optionPair.getSecondOption().controller().formatValue());
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		Dimension<Integer> firstWidgetDimension = widgetDimension.withWidth((int) ((double) widgetDimension.width() * 0.5));
		Dimension<Integer> secondWidgetDimension = widgetDimension.moved(firstWidgetDimension.width(), 0).withWidth((int) ((double) widgetDimension.width() - firstWidgetDimension.width()));

		AbstractWidget labelWidget = this.labelOption.controller().provideWidget(screen, firstWidgetDimension);
		AbstractWidget firstOptionWidget = this.optionPair.getFirstOption().controller().provideWidget(screen, firstWidgetDimension);
		AbstractWidget secondOptionWidget = this.optionPair.getSecondOption().controller().provideWidget(screen, secondWidgetDimension);
		TextScaledButtonWidget resetButtonWidget;

		widgetDimension = widgetDimension.expanded(0, firstOptionWidget.getDimension().height());

		if (this.optionPair.getFirstOption().controller().option().canResetToDefault() && firstOptionWidget.canReset() && this.optionPair.getSecondOption().controller().option().canResetToDefault() && secondOptionWidget.canReset()) {
			firstOptionWidget.setDimension(firstOptionWidget.getDimension().expanded(-10, 0));
			secondOptionWidget.setDimension(secondOptionWidget.getDimension().expanded(-10, 0));

			resetButtonWidget = new TextScaledButtonWidget(screen, secondOptionWidget.getDimension().xLimit() - 10, 0, 20, 20, 2f, Component.literal("\u21BB"), button -> {
				this.optionPair.getFirstOption().requestSetDefault();
				this.optionPair.getSecondOption().requestSetDefault();
			});
			this.optionPair.getFirstOption().addListener((opt, val) -> resetButtonWidget.active = !opt.isPendingValueDefault() && opt.available());
			this.optionPair.getSecondOption().addListener((opt, val) -> resetButtonWidget.active = !opt.isPendingValueDefault() && opt.available());
			resetButtonWidget.active = !this.optionPair.getFirstOption().isPendingValueDefault() && this.optionPair.getFirstOption().available() && !this.optionPair.getSecondOption().isPendingValueDefault() && this.optionPair.getSecondOption().available();
		} else {
			resetButtonWidget = null;
		}

		return new DualControllerElement(widgetDimension, labelWidget, firstOptionWidget, secondOptionWidget, resetButtonWidget);
	}
}