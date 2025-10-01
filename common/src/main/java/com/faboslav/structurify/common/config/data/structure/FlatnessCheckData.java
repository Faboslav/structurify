package com.faboslav.structurify.common.config.data.structure;

public final class FlatnessCheckData
{
	public static final boolean OVERRIDE_GLOBAL_FLATNESS_CHECK_DEFAULT_VALUE = false;
	public final static boolean IS_ENABLED_DEFAULT_VALUE = false;
	public final static boolean ALLOW_NON_SOLID_BLOCKS_DEFAULT_VALUE = false;

	private boolean overrideGlobalFlatnessCheck = OVERRIDE_GLOBAL_FLATNESS_CHECK_DEFAULT_VALUE;
	private boolean isEnabled = IS_ENABLED_DEFAULT_VALUE;
	private boolean allowNonSolidBlocks = ALLOW_NON_SOLID_BLOCKS_DEFAULT_VALUE;

	public FlatnessCheckData() {

	}

	public boolean isUsingDefaultValues() {
		return
			this.overrideGlobalFlatnessCheck == OVERRIDE_GLOBAL_FLATNESS_CHECK_DEFAULT_VALUE
			&& this.isEnabled == IS_ENABLED_DEFAULT_VALUE
			   && this.allowNonSolidBlocks == ALLOW_NON_SOLID_BLOCKS_DEFAULT_VALUE;
	}

	public boolean isOverridingGlobalFlatnessCheck() {
		return this.overrideGlobalFlatnessCheck;
	}

	public void overrideGlobalFlatnessCheck(boolean overrideGlobalFlatnessCheck) {
		this.overrideGlobalFlatnessCheck = overrideGlobalFlatnessCheck;
	}

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void enable(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean areNonSolidBlocksAllowed() {
		return this.allowNonSolidBlocks;
	}

	public void allowNonSolidBlocks(boolean allowNonSolidBlocks) {
		this.allowNonSolidBlocks = allowNonSolidBlocks;
	}
}
