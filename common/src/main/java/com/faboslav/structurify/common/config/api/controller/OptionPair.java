package com.faboslav.structurify.common.config.api.controller;

import dev.isxander.yacl3.api.Option;

public final class OptionPair<K extends Option<?>, V extends Option<?>>
{
	private final K firstOption;
	private final V secondOption;

	public OptionPair(K firstOption, V secondOption) {
		this.firstOption = firstOption;
		this.secondOption = secondOption;
	}

	public K getFirstOption() {
		return firstOption;
	}

	public V getSecondOption() {
		return secondOption;
	}
}
