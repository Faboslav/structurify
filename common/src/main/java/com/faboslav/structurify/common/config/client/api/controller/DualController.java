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

public record DualController<K extends Option<?>, V extends Option<?>>(LabelOption labelOption, OptionPair<K, V> optionPair) implements Controller<OptionPair<K, V>>
{
	@Override
	public Option<OptionPair<K, V>> option() {
		return null;
	}

	@Override
	public Component formatValue() {
		return optionPair.firstOption().controller().formatValue().copy().append(" | ").append(optionPair.secondOption().controller().formatValue());
	}

	@Override
	public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
		Dimension<Integer> firstWidgetDimension = widgetDimension.withWidth((int) ((double) widgetDimension.width() * 0.5));
		Dimension<Integer> secondWidgetDimension = widgetDimension.moved(firstWidgetDimension.width(), 0).withWidth((int) ((double) widgetDimension.width() - firstWidgetDimension.width()));

		AbstractWidget labelWidget = this.labelOption.controller().provideWidget(screen, firstWidgetDimension);
		AbstractWidget firstOptionWidget = this.optionPair.firstOption().controller().provideWidget(screen, firstWidgetDimension);
		AbstractWidget secondOptionWidget = this.optionPair.secondOption().controller().provideWidget(screen, secondWidgetDimension);
		TextScaledButtonWidget resetButtonWidget;

		if (this.optionPair.firstOption().controller().option().canResetToDefault() && firstOptionWidget.canReset() && this.optionPair.secondOption().controller().option().canResetToDefault() && secondOptionWidget.canReset()) {
			firstOptionWidget.setDimension(firstOptionWidget.getDimension().expanded(-10, 0));
			secondOptionWidget.setDimension(secondOptionWidget.getDimension().expanded(-10, 0));

			resetButtonWidget = new TextScaledButtonWidget(screen, secondOptionWidget.getDimension().xLimit() - 10, 0, 20, 20, 2f, Component.literal("\u21BB"), button -> {
				this.optionPair.firstOption().requestSetDefault();
				this.optionPair.secondOption().requestSetDefault();
			});
			this.optionPair.firstOption().addListener((opt, val) -> resetButtonWidget.active = !opt.isPendingValueDefault() && opt.available());
			this.optionPair.secondOption().addListener((opt, val) -> resetButtonWidget.active = !opt.isPendingValueDefault() && opt.available());
			resetButtonWidget.active = !this.optionPair.firstOption().isPendingValueDefault() && this.optionPair.firstOption().available() && !this.optionPair.secondOption().isPendingValueDefault() && this.optionPair.secondOption().available();
		} else {
			resetButtonWidget = null;
		}

		return new DualControllerElement(widgetDimension, labelWidget, firstOptionWidget, secondOptionWidget, resetButtonWidget);
	}
}