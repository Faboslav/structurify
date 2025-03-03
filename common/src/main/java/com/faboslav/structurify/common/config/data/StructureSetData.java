package com.faboslav.structurify.common.config.data;

public final class StructureSetData
{
	public static final boolean OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE = false;
	public static final int MAX_SPACING = 256;
	public static final int MAX_SEPARATION = 256;

	private boolean overrideGlobalSpacingAndSeparationModifier = OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE;
	private final int defaultSpacing;
	private final int defaultSeparation;
	private int spacing;
	private int separation;

	public StructureSetData(int spacing, int separation) {
		this.defaultSpacing = spacing;
		this.defaultSeparation = separation;
		this.spacing = spacing;
		this.separation = separation;
	}

	public boolean isUsingDefaultValues() {
		return this.overrideGlobalSpacingAndSeparationModifier == OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE
			   && this.spacing == defaultSpacing
			   && this.separation == this.defaultSeparation;
	}

	public boolean overrideGlobalSpacingAndSeparationModifier() {
		return this.overrideGlobalSpacingAndSeparationModifier;
	}

	public void setOverrideGlobalSpacingAndSeparationModifier(boolean overrideGlobalSpacingAndSeparationModifier) {
		this.overrideGlobalSpacingAndSeparationModifier = overrideGlobalSpacingAndSeparationModifier;
	}

	public int getDefaultSpacing() {
		return this.defaultSpacing;
	}

	public int getDefaultSeparation() {
		return this.defaultSeparation;
	}

	public int getSpacing() {
		return this.spacing;
	}

	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}

	public int getSeparation() {
		return this.separation;
	}

	public void setSeparation(int separation) {
		this.separation = separation;
	}
}