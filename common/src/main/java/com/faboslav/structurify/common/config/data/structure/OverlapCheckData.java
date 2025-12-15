package com.faboslav.structurify.common.config.data.structure;

public final class OverlapCheckData
{
	public static boolean IS_EXCLUDED_FROM_OVERLAP_PREVENTION_DEFAULT_VALUE = false;

	private boolean isExcludedFromOverlapPrevention = IS_EXCLUDED_FROM_OVERLAP_PREVENTION_DEFAULT_VALUE;
	private boolean defaultIsExcludedFromOverlapPrevention = IS_EXCLUDED_FROM_OVERLAP_PREVENTION_DEFAULT_VALUE;

	public OverlapCheckData() {
	}

	public boolean isUsingDefaultValues() {
		return this.isExcludedFromOverlapPrevention == this.defaultIsExcludedFromOverlapPrevention;
	}

	public void excludeFromOverlapPrevention(boolean isExcludedFromOverlapPrevention) {
		this.isExcludedFromOverlapPrevention = isExcludedFromOverlapPrevention;
	}

	public void defaultExcludeFromOverlapPrevention(boolean defaultIsExcludedFromOverlapPrevention) {
		this.defaultIsExcludedFromOverlapPrevention = defaultIsExcludedFromOverlapPrevention;
	}

	public boolean isExcludedFromOverlapPrevention() {
		return this.isExcludedFromOverlapPrevention;
	}
}
