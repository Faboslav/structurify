package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.config.data.structure.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StructureData implements StructureLikeData
{
	private final List<String> defaultBiomes;
	private final GenerationStep.Decoration defaultStep;
	private final TerrainAdjustment defaultTerrainAdaptation;

	private boolean isDisabled = IS_DISABLED_DEFAULT_VALUE;
	private List<String> biomes;
	private GenerationStep.Decoration step;
	private TerrainAdjustment terrainAdaptation;
	private JigsawData jigsawData;
	private DistanceFromWorldCenterCheckData distanceFromWorldCenterCheckData;
	private OverlapCheckData overlapCheckData;
	private FlatnessCheckData flatnessCheckData;
	private BiomeCheckData biomeCheckData;

	public StructureData(
		List<String> biomes,
		GenerationStep.Decoration step,
		TerrainAdjustment terrainAdaptation
	) {
		this.defaultBiomes = biomes;
		this.biomes = biomes;
		this.defaultStep = step;
		this.step = step;
		this.defaultTerrainAdaptation = terrainAdaptation;
		this.terrainAdaptation = terrainAdaptation;
		this.jigsawData = new JigsawData(0, 0, 0);
		this.distanceFromWorldCenterCheckData = new DistanceFromWorldCenterCheckData();
		this.overlapCheckData = new OverlapCheckData();
		this.flatnessCheckData = new FlatnessCheckData();
		this.biomeCheckData = new BiomeCheckData();
	}

	public boolean isUsingDefaultValues() {
		var biomes = new ArrayList<>(this.biomes);
		var defaultBiomes = new ArrayList<>(this.defaultBiomes);

		Collections.sort(biomes);
		Collections.sort(defaultBiomes);

		return this.isDisabled == IS_DISABLED_DEFAULT_VALUE
			   && this.step == this.defaultStep
			   && this.terrainAdaptation == this.defaultTerrainAdaptation
			   && biomes.equals(defaultBiomes)
			   && (!isJigsawStructure() || (isJigsawStructure() && this.getJigsawData().isUsingDefaultValues()))
			   && this.getDistanceFromWorldCenterCheckData().isUsingDefaultValues()
			   && this.getOverlapCheckData().isUsingDefaultValues()
			   && this.getFlatnessCheckData().isUsingDefaultValues()
			   && this.getBiomeCheckData().isUsingDefaultValues();
	}

	/**
	 * Used in {@link com.faboslav.structurify.common.mixin.ChunkGeneratorMixin} to prevent specific structure generation
	 */
	public boolean isDisabled() {
		return this.isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public List<String> getDefaultBiomes() {
		return this.defaultBiomes;
	}

	public List<String> getBiomes() {
		return this.biomes;
	}

	public void setBiomes(List<String> biomes) {
		this.biomes = biomes;
	}

	public GenerationStep.Decoration getDefaultStep() {
		return this.defaultStep;
	}

	public GenerationStep.Decoration getStep() {
		return this.step;
	}

	public void setStep(GenerationStep.Decoration step) {
		this.step = step;
	}

	public TerrainAdjustment getDefaultTerrainAdaptation() {
		return defaultTerrainAdaptation;
	}

	public TerrainAdjustment getTerrainAdaptation() {
		return terrainAdaptation;
	}

	public void setTerrainAdaptation(TerrainAdjustment terrainAdaptation) {
		this.terrainAdaptation = terrainAdaptation;
	}

	public boolean isJigsawStructure() {
		return this.jigsawData.getSize() != 0 && this.jigsawData.getHorizontalMaxDistanceFromCenter() != 0 && this.jigsawData.getVerticalMaxDistanceFromCenter() != 0;
	}

	public JigsawData getJigsawData() {
		return this.jigsawData;
	}

	public void setJigsawData(JigsawData jigsawData) {
		this.jigsawData = jigsawData;
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