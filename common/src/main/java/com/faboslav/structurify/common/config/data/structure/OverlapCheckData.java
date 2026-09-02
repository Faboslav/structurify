package com.faboslav.structurify.common.config.data.structure;

public final class OverlapCheckData
{
	public static final boolean IS_EXCLUDED_FROM_OVERLAP_PREVENTION_DEFAULT_VALUE = false;
	public static final boolean OVERRIDE_GLOBAL_OVERLAP_PADDING_DEFAULT_VALUE = false;
	public static final int OVERLAP_PADDING_DEFAULT_VALUE = 2;

	public static final int OVERLAP_PADDING_MIN_LIMIT = 0;
	public static final int OVERLAP_PADDING_MAX_LIMIT = 16;

	private boolean isExcludedFromOverlapPrevention = IS_EXCLUDED_FROM_OVERLAP_PREVENTION_DEFAULT_VALUE;
	private boolean defaultIsExcludedFromOverlapPrevention = IS_EXCLUDED_FROM_OVERLAP_PREVENTION_DEFAULT_VALUE;
	private boolean overrideGlobalOverlapPadding = OVERRIDE_GLOBAL_OVERLAP_PADDING_DEFAULT_VALUE;
	private int overlapPadding = OVERLAP_PADDING_DEFAULT_VALUE;

	public OverlapCheckData() {
	}

	public boolean isUsingDefaultValues() {
		return this.isUsingDefaultIsExcludedFromOverlapPrevention()
			   && this.isUsingDefaultOverrideGlobalOverlapPadding()
			   && this.isUsingDefaultOverlapPadding();
	}

	public boolean isUsingDefaultIsExcludedFromOverlapPrevention() {
		return this.isExcludedFromOverlapPrevention == this.defaultIsExcludedFromOverlapPrevention;
	}

	public boolean isUsingDefaultOverrideGlobalOverlapPadding() {
		return this.overrideGlobalOverlapPadding == OVERRIDE_GLOBAL_OVERLAP_PADDING_DEFAULT_VALUE;
	}

	public boolean isUsingDefaultOverlapPadding() {
		return this.overlapPadding == OVERLAP_PADDING_DEFAULT_VALUE;
	}

	public void excludeFromOverlapPrevention(boolean isExcludedFromOverlapPrevention) {
		this.isExcludedFromOverlapPrevention = isExcludedFromOverlapPrevention;
	}

	public boolean defaultIsExcludedFromOverlapPrevention() {
		return this.defaultIsExcludedFromOverlapPrevention;
	}

	public void defaultExcludeFromOverlapPrevention(boolean defaultIsExcludedFromOverlapPrevention) {
		this.defaultIsExcludedFromOverlapPrevention = defaultIsExcludedFromOverlapPrevention;
	}

	public boolean isExcludedFromOverlapPrevention() {
		return this.isExcludedFromOverlapPrevention;
	}

	public boolean isOverridingGlobalOverlapPadding() {
		return this.overrideGlobalOverlapPadding;
	}

	public void overrideGlobalOverlapPadding(boolean overrideGlobalOverlapPadding) {
		this.overrideGlobalOverlapPadding = overrideGlobalOverlapPadding;
	}

	public int getOverlapPadding() {
		return this.overlapPadding;
	}

	public void setOverlapPadding(int overlapPadding) {
		this.overlapPadding = overlapPadding;
	}
}
