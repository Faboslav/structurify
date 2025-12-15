package com.faboslav.structurify.common.config.data;

public final class StructureSetData
{
	public static final boolean OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE = false;
	public static final int MIN_SALT = 0;
	public static final int MAX_SALT = Integer.MAX_VALUE;
	public static final float MIN_FREQUENCY = 0.0F;
	public static final float MAX_FREQUENCY = 1.0F;
	public static final int MAX_SPACING = 1024;
	public static final int MAX_SEPARATION = 1024;

	private boolean overrideGlobalSpacingAndSeparationModifier = OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE;
	private final int defaultSalt;
	private final float defaultFrequency;
	private final int defaultSpacing;
	private final int defaultSeparation;
	private int salt;
	private float frequency;
	private int spacing;
	private int separation;

	public StructureSetData(int salt, float frequency, int spacing, int separation) {
		this.defaultSalt = salt;
		this.defaultFrequency = frequency;
		this.defaultSpacing = spacing;
		this.defaultSeparation = separation;
		this.salt = salt;
		this.frequency = frequency;
		this.spacing = spacing;
		this.separation = separation;
	}

	public boolean isUsingDefaultValues() {
		return this.overrideGlobalSpacingAndSeparationModifier == OVERRIDE_GLOBAL_SPACING_AND_SEPARATION_MODIFIER_DEFAULT_VALUE
			   && this.salt == this.defaultSalt
			   && this.frequency == this.defaultFrequency
			   && this.spacing == this.defaultSpacing
			   && this.separation == this.defaultSeparation;
	}

	public boolean overrideGlobalSpacingAndSeparationModifier() {
		return this.overrideGlobalSpacingAndSeparationModifier;
	}

	public void setOverrideGlobalSpacingAndSeparationModifier(boolean overrideGlobalSpacingAndSeparationModifier) {
		this.overrideGlobalSpacingAndSeparationModifier = overrideGlobalSpacingAndSeparationModifier;
	}

	public int getDefaultSalt() {
		return this.defaultSalt;
	}

	public float getDefaultFrequency() {
		return this.defaultFrequency;
	}

	public int getDefaultSpacing() {
		return this.defaultSpacing;
	}

	public int getDefaultSeparation() {
		return this.defaultSeparation;
	}

	public int getSalt() {
		return this.salt;
	}

	public void setSalt(int salt) {
		this.salt = salt;
	}

	public float getFrequency() {
		return this.frequency;
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
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