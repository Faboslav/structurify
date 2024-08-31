package com.faboslav.structurify.common.config.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StructureData
{
	public static StructureData.BiomeBlacklistType DEFAULT_BIOME_BLACKLIST_TYPE = BiomeBlacklistType.CENTER_PART;

	private boolean isDisabled = false;
	private final Set<String> biomes;
	private List<String> blacklistedBiomes = new ArrayList<>();
	private BiomeBlacklistType biomeBlacklistType = DEFAULT_BIOME_BLACKLIST_TYPE;
	private boolean isBiomeBlacklistTypeLocked = false;

	public StructureData(Set<String> biomes, boolean isBiomeBlacklistTypeLocked) {
		this.biomes = biomes;
		this.isBiomeBlacklistTypeLocked = isBiomeBlacklistTypeLocked;
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

	public BiomeBlacklistType getBiomeBlacklistType() {
		return this.biomeBlacklistType;
	}

	public void setBiomeBlacklistType(BiomeBlacklistType biomeBlacklistType) {
		this.biomeBlacklistType = biomeBlacklistType;
	}

	public void lockBlacklistType() {
		this.isBiomeBlacklistTypeLocked = true;
	}

	public boolean isBiomeBlacklistTypeLocked() {
		return this.isBiomeBlacklistTypeLocked;
	}

	public enum BiomeBlacklistType
	{
		NONE,
		CENTER_PART,
		ALL_PARTS;
	}
}