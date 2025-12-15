package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.DistanceFromWorldCenterCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.faboslav.structurify.common.config.data.structure.OverlapCheckData;

public final class StructureNamespaceData implements StructureLikeData
{
	public static String GLOBAL_NAMESPACE_IDENTIFIER = "global";
	private boolean isDisabled = IS_DISABLED_DEFAULT_VALUE;
	private DistanceFromWorldCenterCheckData distanceFromWorldCenterCheckData;
	private OverlapCheckData overlapCheckData;
	private FlatnessCheckData flatnessCheckData;
	private BiomeCheckData biomeCheckData;

	public StructureNamespaceData() {
		this.distanceFromWorldCenterCheckData = new DistanceFromWorldCenterCheckData();
		this.overlapCheckData = new OverlapCheckData();
		this.flatnessCheckData = new FlatnessCheckData();
		this.biomeCheckData = new BiomeCheckData();
	}

	public boolean isUsingDefaultValues() {
		return this.isDisabled == IS_DISABLED_DEFAULT_VALUE
			   && this.getDistanceFromWorldCenterCheckData().isUsingDefaultValues()
			   && this.getOverlapCheckData().isUsingDefaultValues()
			   && this.getFlatnessCheckData().isUsingDefaultValues()
			   && this.getBiomeCheckData().isUsingDefaultValues();
	}

	public boolean isDisabled() {
		return this.isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public DistanceFromWorldCenterCheckData getDistanceFromWorldCenterCheckData() {
		return distanceFromWorldCenterCheckData;
	}

	public void setDistanceFromWorldCenterCheckData(DistanceFromWorldCenterCheckData distanceFromWorldCenterCheckData) {
		this.distanceFromWorldCenterCheckData = distanceFromWorldCenterCheckData;
	}

	public OverlapCheckData getOverlapCheckData() {
		return this.overlapCheckData;
	}

	public void setOverlapCheckData(OverlapCheckData overlapCheckData) {
		this.overlapCheckData = overlapCheckData;
	}

	public FlatnessCheckData getFlatnessCheckData() {
		return this.flatnessCheckData;
	}

	public void setFlatnessCheckData(FlatnessCheckData flatnessCheckData) {
		this.flatnessCheckData = flatnessCheckData;
	}

	public BiomeCheckData getBiomeCheckData() {
		return this.biomeCheckData;
	}

	public void setBiomeCheckData(BiomeCheckData biomeCheckData) {
		this.biomeCheckData = biomeCheckData;
	}
}
