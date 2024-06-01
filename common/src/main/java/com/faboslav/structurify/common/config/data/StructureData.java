package com.faboslav.structurify.common.config.data;

import java.util.Set;

public class StructureData
{
	private boolean isDisabled;
	private final Set<String> biomes;
	private Set<String> disabledBiomes;

	public StructureData(boolean isDisabled, Set<String> biomes, Set<String> disabledBiomes) {
		this.isDisabled = isDisabled;
		this.biomes = biomes;
		this.disabledBiomes = disabledBiomes;
	}

	public boolean isDisabled() {
		return this.isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public Set<String> getBiomes() {
		return this.biomes;
	}

	public Set<String> getDisabledBiomes() {
		return this.disabledBiomes;
	}

	public void setDisabledBiomes(Set<String> disabledBiomes) {
		this.disabledBiomes = disabledBiomes;
	}
}