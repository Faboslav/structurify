package com.faboslav.structurify.common.config.client.api.controller.builder;

import com.faboslav.structurify.common.config.client.api.controller.DualController;
import com.faboslav.structurify.common.config.client.api.option.OptionPair;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;

public class DualControllerBuilder<K extends Option<?>, V extends Option<?>> implements ControllerBuilder<OptionPair<K, V>>
{
	private final K firstOption;
	private final V secondOption;

	public DualControllerBuilder(K firstOption, V secondOption) {
		this.firstOption = firstOption;
		this.secondOption = secondOption;
	}

	@Override
	public Controller<OptionPair<K, V>> build() {
		return new DualController<>(new OptionPair<>(firstOption, secondOption));
	}

	public static <K extends Option<?>, V extends Option<?>> DualControllerBuilder<K, V> create(
		K firstOption,
		V secondOption
	) {
		return new DualControllerBuilder<>( firstOption, secondOption);
	}
}