package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.DistanceFromWorldCenterCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.faboslav.structurify.common.config.data.structure.OverlapCheckData;

public interface StructureLikeData
{
	boolean IS_DISABLED_DEFAULT_VALUE = false;

	boolean isDisabled();

	void setDisabled(boolean isDisabled);

	DistanceFromWorldCenterCheckData getDistanceFromWorldCenterCheckData();

	void setDistanceFromWorldCenterCheckData(DistanceFromWorldCenterCheckData distanceFromWorldCenterCheckData);

	OverlapCheckData getOverlapCheckData();

	void setOverlapCheckData(OverlapCheckData overlapCheckData);

	FlatnessCheckData getFlatnessCheckData();

	void setFlatnessCheckData(FlatnessCheckData flatnessCheckData);

	BiomeCheckData getBiomeCheckData();

	void setBiomeCheckData(BiomeCheckData biomeCheckData);
}