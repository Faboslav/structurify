package com.faboslav.structurify.common.config.data;

public final class StructureSetData
{
	public static final int MAX_SPACING = 128;
	public static final int MAX_SEPARATION = 128;

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

	public boolean isUsingDefaultSpacing() {
		return this.spacing == this.defaultSpacing;
	}

	public boolean isUsingDefaultSeparation() {
		return this.separation == this.defaultSeparation;
	}

	public boolean isUsingDefaultSpacingAndSeparation() {
		return isUsingDefaultSpacing() && isUsingDefaultSeparation();
	}
}