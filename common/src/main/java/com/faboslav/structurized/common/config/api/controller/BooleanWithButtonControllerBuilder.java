package com.faboslav.structurized.common.config.api.controller;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import dev.isxander.yacl3.impl.controller.BooleanControllerBuilderImpl;
import org.apache.commons.lang3.Validate;

public class BooleanWithButtonControllerBuilder extends BooleanControllerBuilderImpl
{
	private boolean coloured = false;
	private ValueFormatter<Boolean> formatter = BooleanController.ON_OFF_FORMATTER::apply;

	public BooleanWithButtonControllerBuilder(Option<Boolean> option) {
		super(option);
	}

	public BooleanWithButtonControllerBuilder coloured(boolean coloured) {
		this.coloured = coloured;
		return this;
	}

	public BooleanWithButtonControllerBuilder formatValue(ValueFormatter<Boolean> formatter) {
		Validate.notNull(formatter, "formatter cannot be null");

		this.formatter = formatter;
		return this;
	}

	public BooleanWithButtonControllerBuilder onOffFormatter() {
		this.formatter = BooleanController.ON_OFF_FORMATTER::apply;
		return this;
	}

	public BooleanWithButtonControllerBuilder yesNoFormatter() {
		this.formatter = BooleanController.YES_NO_FORMATTER::apply;
		return this;
	}

	public BooleanWithButtonControllerBuilder trueFalseFormatter() {
		this.formatter = BooleanController.TRUE_FALSE_FORMATTER::apply;
		return this;
	}

	@Override
    public Controller<Boolean> build() {
        return new BooleanWithButtonController(option, this.formatter::format, this.coloured);
    }

	public static BooleanWithButtonControllerBuilder create(Option<Boolean> option) {
		return new BooleanWithButtonControllerBuilder(option);
	}
}
