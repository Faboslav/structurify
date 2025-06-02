package com.faboslav.structurify.common.config.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StructureData
{
	public final static boolean IS_DISABLED_DEFAULT_VALUE = false;
	public final static boolean ENABLE_FLATNESS_CHECK_DEFAULT_VALUE = false;
	public final static int FLATNESS_CHECK_THRESHOLD_DEFAULT_VALUE = 10;
	public final static boolean ENABLE_BIOME_CHECK_DEFAULT_VALUE = false;
	public final static boolean ALLOW_AIR_BLOCKS_IN_FLATNESS_CHECK_DEFAULT_VALUE = false;
	public final static boolean ALLOW_LIQUID_BLOCKS_IN_FLATNESS_CHECK_DEFAULT_VALUE = false;

	private final List<String> defaultBiomes;
	private final int defaultCheckDistance;

	private boolean isDisabled = IS_DISABLED_DEFAULT_VALUE;
	private boolean enableFlatnessCheck = ENABLE_FLATNESS_CHECK_DEFAULT_VALUE;
	private int flatnessCheckDistance;
	private int flatnessCheckThreshold = FLATNESS_CHECK_THRESHOLD_DEFAULT_VALUE;
	private boolean allowAirBlocksInFlatnessCheck = ALLOW_AIR_BLOCKS_IN_FLATNESS_CHECK_DEFAULT_VALUE;
	private boolean allowLiquidBlocksInFlatnessCheck = ALLOW_LIQUID_BLOCKS_IN_FLATNESS_CHECK_DEFAULT_VALUE;
	private boolean enableBiomeCheck = ENABLE_BIOME_CHECK_DEFAULT_VALUE;
	private int biomeCheckDistance;
	private List<String> biomes;

	public StructureData(List<String> biomes, int checkDistance) {
		this.defaultBiomes = biomes;
		this.biomes = biomes.stream().toList();
		this.defaultCheckDistance = checkDistance;
		this.flatnessCheckDistance = checkDistance;
		this.biomeCheckDistance = checkDistance;
	}

	public boolean isUsingDefaultValues() {
		var biomes = new ArrayList<>(this.biomes);
		var defaultBiomes = new ArrayList<>(this.defaultBiomes);

		Collections.sort(biomes);
		Collections.sort(defaultBiomes);

		return this.isDisabled == IS_DISABLED_DEFAULT_VALUE
			&& this.enableFlatnessCheck == ENABLE_FLATNESS_CHECK_DEFAULT_VALUE
			&& this.flatnessCheckDistance == this.defaultCheckDistance
			&& this.flatnessCheckThreshold == FLATNESS_CHECK_THRESHOLD_DEFAULT_VALUE
			&& this.enableBiomeCheck == ENABLE_BIOME_CHECK_DEFAULT_VALUE
			&& this.biomeCheckDistance == this.defaultCheckDistance
			&& biomes.equals(defaultBiomes);
	}

	public boolean isDisabled() {
		return this.isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public int getDefaultCheckDistance() {
		return this.defaultCheckDistance;
	}

	public boolean isFlatnessCheckEnabled() {
		return this.enableFlatnessCheck;
	}

	public void setEnableFlatnessCheck(boolean enableFlatnessCheck) {
		this.enableFlatnessCheck = enableFlatnessCheck;
	}

	public boolean areAirBlocksAllowedInFlatnessCheck() {
		return this.allowAirBlocksInFlatnessCheck;
	}

	public void setAllowAirBlocksInFlatnessCheck(boolean allowAirBlocksInFlatnessCheck) {
		this.allowAirBlocksInFlatnessCheck = allowAirBlocksInFlatnessCheck;
	}

	public boolean areLiquidBlocksAllowedInFlatnessCheck() {
		return this.allowLiquidBlocksInFlatnessCheck;
	}

	public void setAllowLiquidBlocksInFlatnessCheck(boolean allowLiquidBlocksInFlatnessCheck) {
		this.allowLiquidBlocksInFlatnessCheck = allowLiquidBlocksInFlatnessCheck;
	}

	public int getFlatnessCheckDistance() {
		return this.flatnessCheckDistance;
	}

	public void setFlatnessCheckDistance(int flatnessCheckDistance) {
		this.flatnessCheckDistance = flatnessCheckDistance;
	}

	public int getFlatnessCheckThreshold() {
		return this.flatnessCheckThreshold;
	}

	public void setFlatnessCheckThreshold(int flatnessCheckThreshold) {
		this.flatnessCheckThreshold = flatnessCheckThreshold;
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

	public List<String> getDefaultBiomes() {
		return this.defaultBiomes;
	}

	public List<String> getBiomes() {
		return this.biomes;
	}

	public void setBiomes(List<String> biomes) {
		this.biomes = biomes;
	}
}