package com.faboslav.structurify.common.config.data.structure;

public class DistanceFromWorldCenterCheckData
{
	public static final boolean OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE = false;
	public static final int MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE = 0;
	public static final int MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE = 0;

	private boolean overrideGlobalDistanceFromWorldCenter = OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;
	private int minDistanceFromWorldCenter = MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;
	private int maxDistanceFromWorldCenter = MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;

	public DistanceFromWorldCenterCheckData() {
	}

	public boolean isUsingDefaultValues() {
		return
			this.overrideGlobalDistanceFromWorldCenter == OVERRIDE_GLOBAL_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE
			&& this.minDistanceFromWorldCenter == MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE
			&& this.maxDistanceFromWorldCenter == MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;
	}

	public boolean isEnabled() {
		return this.minDistanceFromWorldCenter != MIN_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE || this.maxDistanceFromWorldCenter != MAX_DISTANCE_FROM_WORLD_CENTER_DEFAULT_VALUE;
	}

	public boolean isOverridingGlobalDistanceFromWorldCenter() {
		return this.overrideGlobalDistanceFromWorldCenter;
	}

	public void overrideGlobalDistanceFromWorldCenter(boolean overrideGlobalDistanceFromWorldCenter) {
		this.overrideGlobalDistanceFromWorldCenter = overrideGlobalDistanceFromWorldCenter;
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
}
