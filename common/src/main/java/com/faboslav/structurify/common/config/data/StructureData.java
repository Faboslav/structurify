package com.faboslav.structurify.common.config.data;

import java.util.List;
import java.util.Set;

public class StructureData
{
	private boolean isDisabled;
	private final Set<String> biomes;
	private List<String> blacklistedBiomes;

	public StructureData(boolean isDisabled, Set<String> biomes, List<String> blacklistedBiomes) {
		this.isDisabled = isDisabled;
		this.biomes = biomes;
		this.blacklistedBiomes = blacklistedBiomes;
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

	public List<String> getBlacklistedBiomes() {
		return this.blacklistedBiomes;
	}

	public void setBlacklistedBiomes(List<String> blacklistedBiomes) {
		this.blacklistedBiomes = blacklistedBiomes;
	}
}