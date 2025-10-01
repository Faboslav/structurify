package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;

import java.util.List;

public final class StructureNamespaceData implements StructureLikeData
{
	public static String GLOBAL_NAMESPACE_IDENTIFIER = "global";
	private boolean isDisabled = IS_DISABLED_DEFAULT_VALUE;
	private FlatnessCheckData flatnessCheckData;
	private BiomeCheckData biomeCheckData;

	public StructureNamespaceData() {
		this.flatnessCheckData = new FlatnessCheckData();
		this.biomeCheckData = new BiomeCheckData();
	}

	public boolean isUsingDefaultValues() {
		return this.isDisabled == IS_DISABLED_DEFAULT_VALUE
			   && this.getFlatnessCheckData().isUsingDefaultValues()
			   && this.getBiomeCheckData().isUsingDefaultValues();
	}

	public boolean isDisabled() {
		return this.isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
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
