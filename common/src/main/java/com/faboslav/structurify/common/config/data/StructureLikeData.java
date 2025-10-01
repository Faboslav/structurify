package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;

public interface StructureLikeData
{
	boolean IS_DISABLED_DEFAULT_VALUE = false;

	boolean isDisabled();

	void setDisabled(boolean isDisabled);

	FlatnessCheckData getFlatnessCheckData();

	void setFlatnessCheckData(FlatnessCheckData flatnessCheckData);

	BiomeCheckData getBiomeCheckData();

	void setBiomeCheckData(BiomeCheckData biomeCheckData);
}