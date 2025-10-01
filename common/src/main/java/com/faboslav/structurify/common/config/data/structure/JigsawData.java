package com.faboslav.structurify.common.config.data.structure;

import com.faboslav.structurify.common.Structurify;

public final class JigsawData
{
	public static final int MIN_SIZE = 0;
	public static final int MAX_SIZE = 20;
	public static final int MIN_HORIZONTAL_MAX_DISTANCE_FROM_CENTER = 1;
	public static final int MAX_HORIZONTAL_MAX_DISTANCE_FROM_CENTER = 128;
	public static final int MIN_VERTICAL_MAX_DISTANCE_FROM_CENTER = 1;
	public static final int MAX_VERTICAL_MAX_DISTANCE_FROM_CENTER = 4096;

	private final int defaultSize;
	private final int defaultHorizontalMaxDistanceFromCenter;
	private final int defaultVerticalMaxDistanceFromCenter;

	private int size;
	private int horizontalMaxDistanceFromCenter;
	private int verticalMaxDistanceFromCenter;

	public JigsawData(int size, int horizontalMaxDistanceFromCenter, int verticalMaxDistanceFromCenter) {
		this.defaultSize = size;
		this.size = size;
		this.defaultHorizontalMaxDistanceFromCenter = horizontalMaxDistanceFromCenter;
		this.horizontalMaxDistanceFromCenter = horizontalMaxDistanceFromCenter;
		this.defaultVerticalMaxDistanceFromCenter = verticalMaxDistanceFromCenter;
		this.verticalMaxDistanceFromCenter = verticalMaxDistanceFromCenter;
	}

	public boolean isUsingDefaultValues() {
		return this.size == this.defaultSize
			   && this.horizontalMaxDistanceFromCenter == this.defaultHorizontalMaxDistanceFromCenter
			   && this.verticalMaxDistanceFromCenter == this.defaultVerticalMaxDistanceFromCenter;
	}

	public int getDefaultSize() {
		return this.defaultSize;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getDefaultHorizontalMaxDistanceFromCenter() {
		return this.defaultHorizontalMaxDistanceFromCenter;
	}

	public int getHorizontalMaxDistanceFromCenter() {
		return this.horizontalMaxDistanceFromCenter;
	}

	public void setHorizontalMaxDistanceFromCenter(int horizontalMaxDistanceFromCenter) {
		this.horizontalMaxDistanceFromCenter = horizontalMaxDistanceFromCenter;
	}

	public int getDefaultVerticalMaxDistanceFromCenter() {
		return this.defaultVerticalMaxDistanceFromCenter;
	}

	public int getVerticalMaxDistanceFromCenter() {
		return this.verticalMaxDistanceFromCenter;
	}

	public void setVerticalMaxDistanceFromCenter(int verticalMaxDistanceFromCenter) {
		this.verticalMaxDistanceFromCenter = verticalMaxDistanceFromCenter;
	}
}
