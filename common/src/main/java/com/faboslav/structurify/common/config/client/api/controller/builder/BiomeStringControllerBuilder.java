package com.faboslav.structurify.common.config.client.api.controller.builder;

import com.faboslav.structurify.common.config.client.api.controller.BiomeStringController;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl;

public final class BiomeStringControllerBuilder extends AbstractControllerBuilderImpl<String>
{
	private BiomeStringControllerBuilder(Option<String> option) {
		super(option);
	}

	@Override
	public Controller<String> build() {
		return new BiomeStringController(option);
	}

	public static BiomeStringControllerBuilder create(Option<String> option) {
		return new BiomeStringControllerBuilder(option);
	}
}