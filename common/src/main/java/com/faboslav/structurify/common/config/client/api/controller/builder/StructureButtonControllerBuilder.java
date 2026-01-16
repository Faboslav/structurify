package com.faboslav.structurify.common.config.client.api.controller.builder;

import com.faboslav.structurify.common.config.client.api.controller.StructureButtonController;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import dev.isxander.yacl3.impl.controller.BooleanControllerBuilderImpl;
import org.apache.commons.lang3.Validate;

public class StructureButtonControllerBuilder extends BooleanControllerBuilderImpl
{
	private final String structureId;
	private boolean coloured = false;
	private ValueFormatter<Boolean> formatter = BooleanController.ON_OFF_FORMATTER::apply;
	private StructureButtonController.OpenConfigCallback openConfigCallback;
	private String buttonTooltip;

	public StructureButtonControllerBuilder(Option<Boolean> option, String structureId) {
		super(option);

		this.structureId = structureId;
	}

	public StructureButtonControllerBuilder coloured(boolean coloured) {
		this.coloured = coloured;
		return this;
	}

	public StructureButtonControllerBuilder formatValue(ValueFormatter<Boolean> formatter) {
		Validate.notNull(formatter, "formatter cannot be null");

		this.formatter = formatter;
		return this;
	}

	public StructureButtonControllerBuilder onOffFormatter() {
		this.formatter = BooleanController.ON_OFF_FORMATTER::apply;
		return this;
	}

	public StructureButtonControllerBuilder yesNoFormatter() {
		this.formatter = BooleanController.YES_NO_FORMATTER::apply;
		return this;
	}

	public StructureButtonControllerBuilder trueFalseFormatter() {
		this.formatter = BooleanController.TRUE_FALSE_FORMATTER::apply;
		return this;
	}

	public StructureButtonControllerBuilder openConfigCallback(StructureButtonController.OpenConfigCallback openConfigCallback) {
		this.openConfigCallback = openConfigCallback;
		return this;
	}

	public StructureButtonControllerBuilder buttonTooltip(String buttonTooltip) {
		this.buttonTooltip = buttonTooltip;
		return this;
	}

	@Override
	public Controller<Boolean> build() {
		return new StructureButtonController(option, this.structureId, this.formatter::format, this.coloured, this.openConfigCallback, this.buttonTooltip);
	}

	public static StructureButtonControllerBuilder create(Option<Boolean> option, String structureId) {
		return new StructureButtonControllerBuilder(option, structureId);
	}
}
