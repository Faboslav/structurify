package com.faboslav.structurify.world.level.structure.checks;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.faboslav.structurify.common.util.BiomeUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class StructureChecker
{
	public static boolean checkStructure(
		StructureStart structureStart,
		@Nullable ResourceLocation structureId,
		StructurifyStructure structure,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		BiomeSource biomeSource
	) {
		if(structureId == null) {
			structureId = structure.structurify$getStructureIdentifier();
		}

		StructureCheckData structureCheckData = new StructureCheckData(structureId, structure, structureStart);

		var biomeCheckResult = checkBiomes(structureCheckData, biomeSource, randomState);

		if (!biomeCheckResult) {
			return false;
		}

		var flatnessCheckResult = checkFlatness(structureCheckData, chunkGenerator, heightAccessor, randomState);

		if (!flatnessCheckResult) {
			return false;
		}

		var overlapCheckResult = checkOverlap(structureCheckData, chunkGenerator);

		return overlapCheckResult;
	}

	public static void debugCheckStructure(
		StructureStart structureStart,
		StructurifyStructure structure,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		BiomeSource biomeSource
	) {
		ResourceLocation structureId = structure.structurify$getStructureIdentifier();
		StructureCheckData structureCheckData = new StructureCheckData(structureId, structure, structureStart);

		checkBiomes(structureCheckData, biomeSource, randomState);
		checkFlatness(structureCheckData, chunkGenerator, heightAccessor, randomState);
		checkOverlap(structureCheckData, chunkGenerator);
	}

	private static boolean checkBiomes(
		StructureCheckData structureCheckData,
		BiomeSource biomeSource,
		RandomState randomState
	) {
		var structure = structureCheckData.getStructure();
		var structureData = structure.structurify$getStructureData();
		BiomeCheckData biomeCheckData = StructureBiomeCheck.getBiomeCheckData(structureCheckData);

		if (biomeCheckData == null || !biomeCheckData.isEnabled()) {
			return true;
		}

		if (biomeSource instanceof CheckerboardColumnBiomeSource) {
			return true;
		}

		var structureStep = structureData.getStep();

		if (structureStep != GenerationStep.Decoration.SURFACE_STRUCTURES && structureStep != GenerationStep.Decoration.LOCAL_MODIFICATIONS) {
			return true;
		}

		boolean isOceanStructure = structure.structurify$getStructureBiomes().stream()
			.map(h -> h.unwrapKey().orElse(null))
			.anyMatch(k -> k != null && BiomeUtil.getOceanBiomes().contains(k));

		if (isOceanStructure) {
			return true;
		}

		var biomeCheckResult = StructureBiomeCheck.checkBiomes(structureCheckData, biomeCheckData, biomeSource, randomState);

		if(!biomeCheckResult) {
			return false;
		}

		/*
		if(Structurify.getConfig().getDebugData().isEnabled()) {
			var structureKey = ChunkPos.asLong(structureCheckData.getStructureCenter());
			Structurify.getDebugRenderer().removeStructureFlatnessCheckInfo(structureKey);
			Structurify.getDebugRenderer().removeStructureFlatnessCheckSamples(structureKey);
		}*/
		return true;
	}

	private static boolean checkFlatness(
		StructureCheckData structureCheckData,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState
	) {
		var structure = structureCheckData.getStructure();
		var structureData = structure.structurify$getStructureData();
		FlatnessCheckData flatnessCheckData = StructureFlatnessCheck.getFlatnessCheckData(structureCheckData);

		if (flatnessCheckData == null || !flatnessCheckData.isEnabled()) {
			return true;
		}

		var structureStep = structureData.getStep();

		if (structureStep != GenerationStep.Decoration.SURFACE_STRUCTURES && structureStep != GenerationStep.Decoration.LOCAL_MODIFICATIONS) {
			return true;
		}

		boolean isOceanStructure = structure.structurify$getStructureBiomes().stream()
			.map(h -> h.unwrapKey().orElse(null))
			.anyMatch(k -> k != null && BiomeUtil.getOceanBiomes().contains(k));

		if (isOceanStructure) {
			return true;
		}

		boolean flatnessCheckResult = StructureFlatnessCheck.checkFlatness(
			structureCheckData,
			flatnessCheckData,
			chunkGenerator,
			heightAccessor,
			randomState
		);

		/*
		if(Structurify.getConfig().getDebugData().isEnabled()) {
			var structureKey = ChunkPos.asLong(structureCheckData.getStructureCenter());

			Structurify.getDebugRenderer().removeStructureBiomeCheckOverview(structureKey);
			Structurify.getDebugRenderer().removeStructureBiomeCheckSamples(structureKey);
		}*/
		return flatnessCheckResult;
	}

	private static boolean checkOverlap(
		StructureCheckData structureCheckData,
		ChunkGenerator chunkGenerator
	) {
		if (!Structurify.getConfig().preventStructureOverlap) {
			return true;
		}

		boolean overlapCheckResult = StructureOverlapCheck.checkForOverlap(structureCheckData, (StructurifyChunkGenerator) chunkGenerator);

		/*
		if(Structurify.getConfig().getDebugData().isEnabled()) {
			var structureKey = ChunkPos.asLong(structureCheckData.getStructureCenter());

			Structurify.getDebugRenderer().removeStructureFlatnessCheckInfo(structureKey);
			Structurify.getDebugRenderer().removeStructureFlatnessCheckSamples(structureKey);
			Structurify.getDebugRenderer().removeStructureBiomeCheckOverview(structureKey);
			Structurify.getDebugRenderer().removeStructureBiomeCheckSamples(structureKey);
		}*/
		return !overlapCheckResult;
	}
}
