package com.faboslav.structurify.common.config.data.structure;

public class DistanceFromWorldCenterCheckData
{
	public static final boolean OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE = false;
	public final static DistanceFromWorldCenterCheckMode MODE_DEFAULT_VALUE = DistanceFromWorldCenterCheckMode.CIRCLE;
	public static final boolean ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE = false;
	public static final boolean ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE = false;
	public static final int MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE = 0;
	public static final int MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE = 0;

	public static final int DISTANCE_FROM_WORLD_CENTER_MIN_LIMIT = 0;
	public static final int DISTANCE_FROM_WORLD_CENTER_MAX_LIMIT = 30000000;

	private boolean overrideGlobalDistanceFromWorldCenter = OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;
	private DistanceFromWorldCenterCheckMode mode = MODE_DEFAULT_VALUE;
	private boolean enableMinDistanceFromWorldCenter = ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;
	private boolean enableMaxDistanceFromWorldCenter = ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;
	private int minDistanceFromWorldCenter = MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;
	private int maxDistanceFromWorldCenter = MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;

	public DistanceFromWorldCenterCheckData() {
	}

	public boolean isUsingDefaultValues() {
		return
			this.overrideGlobalDistanceFromWorldCenter == OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE
			&& this.mode == MODE_DEFAULT_VALUE
			&& this.enableMinDistanceFromWorldCenter == ENABLE_MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE
			&& this.enableMaxDistanceFromWorldCenter == ENABLE_MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE
			&& this.minDistanceFromWorldCenter == MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE
			&& this.maxDistanceFromWorldCenter == MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;
	}

	public boolean isEnabled() {
		return this.enableMinDistanceFromWorldCenter || this.enableMaxDistanceFromWorldCenter;
	}

	public boolean isOverridingGlobalDistanceFromWorldCenter() {
		return this.overrideGlobalDistanceFromWorldCenter;
	}

	public void overrideGlobalDistanceFromWorldCenter(boolean overrideGlobalDistanceFromWorldCenter) {
		this.overrideGlobalDistanceFromWorldCenter = overrideGlobalDistanceFromWorldCenter;
	}

	public DistanceFromWorldCenterCheckMode getMode() {
		return this.mode;
	}

	public void setMode(DistanceFromWorldCenterCheckMode mode) {
		this.mode = mode;
	}

	public boolean isMinDistanceFromWorldCenterEnabled() {
		return this.enableMinDistanceFromWorldCenter;
	}

	public void enableMinDistanceFromWorldCenter(boolean enableMinDistanceFromWorldCenter) {
		this.enableMinDistanceFromWorldCenter = enableMinDistanceFromWorldCenter;
	}

	public boolean isMaxDistanceFromWorldCenterEnabled() {
		return this.enableMaxDistanceFromWorldCenter;
	}

	public void enableMaxDistanceFromWorldCenter(boolean enableMaxDistanceFromWorldCenter) {
		this.enableMaxDistanceFromWorldCenter = enableMaxDistanceFromWorldCenter;
	}

	public void setMinDistanceFromWorldCenter(int minDistanceFromWorldCenter) {
		this.minDistanceFromWorldCenter = minDistanceFromWorldCenter;
	}

	public int getMinDistanceFromWorldCenter() {
		return this.minDistanceFromWorldCenter;
	}

	public void setMaxDistanceFromWorldCenter(int maxDistanceFromWorldCenter) {
		this.maxDistanceFromWorldCenter = maxDistanceFromWorldCenter;
	}

	public int getMaxDistanceFromWorldCenter() {
		return this.maxDistanceFromWorldCenter;
	}

	public enum DistanceFromWorldCenterCheckMode
	{
		CIRCLE,
		SQUARE
	}
}
