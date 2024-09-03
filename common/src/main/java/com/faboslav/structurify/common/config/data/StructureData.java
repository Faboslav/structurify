package com.faboslav.structurify.common.config.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StructureData
{
	private boolean isDisabled = false;
	private final Set<String> defaultBiomes;
	private boolean enableBiomeCheck = false;
	private int biomeCheckDistance;
	private List<String> biomes = new ArrayList<>();

	public StructureData(Set<String> biomes, int biomeCheckDistance) {
		this.defaultBiomes = biomes;
		this.biomeCheckDistance = biomeCheckDistance;
	}

	public boolean isDisabled() {
		return this.isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public boolean isBiomeCheckEnabled() {
		return this.enableBiomeCheck;
	}

	public void setEnableBiomeCheck(boolean enableBiomeCheck) {
		this.enableBiomeCheck = enableBiomeCheck;
	}

	public int getBiomeCheckDistance() {
		return this.biomeCheckDistance;
	}

	public void setBiomeCheckDistance(int biomeCheckDistance) {
		this.biomeCheckDistance = biomeCheckDistance;
	}

	public Set<String> getDefaultBiomes() {
		return this.defaultBiomes;
	}

	public List<String> getBiomes() {
		return this.biomes;
	}

	public void setBiomes(List<String> biomes) {
		this.biomes = biomes;
	}
}