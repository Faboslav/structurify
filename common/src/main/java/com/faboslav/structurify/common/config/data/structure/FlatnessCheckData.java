package com.faboslav.structurify.common.config.data.structure;

public final class FlatnessCheckData
{
	public static final boolean OVERRIDE_GLOBAL_FLATNESS_CHECK_DEFAULT_VALUE = false;
	public final static boolean IS_ENABLED_DEFAULT_VALUE = false;
	public final static boolean ALLOW_NON_SOLID_BLOCKS_DEFAULT_VALUE = false;
	public final static FlatnessCheckMode MODE_DEFAULT_VALUE = FlatnessCheckMode.AUTO;
	public final static int MAX_HEIGHT_DIFFERENCE_DEFAULT_VALUE = 21;

	public static final int MIN_HEIGHT_DIFFERENCE = 0;
	public static final int MAX_HEIGHT_DIFFERENCE = 128;

	private boolean overrideGlobalFlatnessCheck = OVERRIDE_GLOBAL_FLATNESS_CHECK_DEFAULT_VALUE;
	private boolean defaultOverrideGlobalFlatnessCheck = OVERRIDE_GLOBAL_FLATNESS_CHECK_DEFAULT_VALUE;
	private boolean isEnabled = IS_ENABLED_DEFAULT_VALUE;
	private boolean defaultIsEnabled = IS_ENABLED_DEFAULT_VALUE;
	private FlatnessCheckMode mode = MODE_DEFAULT_VALUE;
	private int maxHeightDifference = MAX_HEIGHT_DIFFERENCE_DEFAULT_VALUE;
	private boolean allowNonSolidBlocks = ALLOW_NON_SOLID_BLOCKS_DEFAULT_VALUE;

	public FlatnessCheckData() {
	}

	public boolean isUsingDefaultValues() {
		return
			this.overrideGlobalFlatnessCheck == this.defaultOverrideGlobalFlatnessCheck
			&& this.isEnabled == this.defaultIsEnabled
			&& this.allowNonSolidBlocks == ALLOW_NON_SOLID_BLOCKS_DEFAULT_VALUE
			&& this.mode == MODE_DEFAULT_VALUE
			&& this.maxHeightDifference == MAX_HEIGHT_DIFFERENCE_DEFAULT_VALUE;
	}

	public boolean isOverridingGlobalFlatnessCheck() {
		return this.overrideGlobalFlatnessCheck;
	}

	public void overrideGlobalFlatnessCheck(boolean overrideGlobalFlatnessCheck) {
		this.overrideGlobalFlatnessCheck = overrideGlobalFlatnessCheck;
	}

	public boolean defaultIsOverridingGlobalFlatnessCheck() {
		return this.defaultOverrideGlobalFlatnessCheck;
	}

	public void defaultOverrideGlobalFlatnessCheck(boolean defaultOverrideGlobalFlatnessCheck) {
		this.defaultOverrideGlobalFlatnessCheck = defaultOverrideGlobalFlatnessCheck;
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void enable(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean defaultIsEnabled() {
		return this.defaultIsEnabled;
	}

	public void defaultEnable(boolean defaultIsEnabled) {
		this.defaultIsEnabled = defaultIsEnabled;
	}

	public boolean areNonSolidBlocksAllowed() {
		return this.allowNonSolidBlocks;
	}

	public FlatnessCheckMode getMode() {
		return this.mode;
	}

	public void setMode(FlatnessCheckMode mode) {
		this.mode = mode;
	}

	public int getMaxHeightDifference() {
		return this.maxHeightDifference;
	}

	public void setMaxHeightDifference(int maxHeightDifference) {
		this.maxHeightDifference = maxHeightDifference;
	}

	public void allowNonSolidBlocks(boolean allowNonSolidBlocks) {
		this.allowNonSolidBlocks = allowNonSolidBlocks;
	}

	public enum FlatnessCheckMode
	{
		AUTO,
		MANUAL
	}
}
