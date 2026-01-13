package com.faboslav.structurify.common.config.client.api.controller.builder;

import com.faboslav.structurify.common.config.client.api.controller.DualController;
import com.faboslav.structurify.common.config.client.api.option.OptionPair;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;

public final class DualControllerBuilder<K extends Option<?>, V extends Option<?>> implements ControllerBuilder<OptionPair<K, V>>
{
	private final OptionPair<K, V> optionPair;

	public DualControllerBuilder(OptionPair<K, V> optionPair) {
		this.optionPair = optionPair;
	}

	@Override
	public Controller<OptionPair<K, V>> build() {
		return new DualController<>(optionPair);
	}

	public static <K extends Option<?>, V extends Option<?>> DualControllerBuilder<K, V> create(OptionPair<K, V> optionPair) {
		return new DualControllerBuilder<>(optionPair);
	}
}