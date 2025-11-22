package com.faboslav.structurify.world.level.structure.checks;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.Nullable;

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

		if(structureId == null) {
			return true;
		}

		StructureCheckData structureCheckData = new StructureCheckData(structureId, structure, structureStart);
		long structureCheckId = generateStructureCheckId(structureId, structureCheckData.getStructureCenter());
		StructurifyChunkGenerator structurifyChunkGenerator = (StructurifyChunkGenerator) chunkGenerator;

		var cachedBiomeChecks = structurifyChunkGenerator.structurify$getBiomeChecks();
		boolean biomeCheckResult = cachedBiomeChecks.computeIfAbsent(structureCheckId, id -> checkBiomes(structureCheckData, biomeSource, randomState));

		if (!biomeCheckResult) {
			return false;
		}

		var cachedFlatnessChecks = structurifyChunkGenerator.structurify$getFlatnessChecks();
		boolean flatnessCheckResult = cachedFlatnessChecks.computeIfAbsent(structureCheckId, id -> checkFlatness(structureCheckData, chunkGenerator, heightAccessor, randomState));

		if (!flatnessCheckResult) {
			return false;
		}

		var cachedOverlapChecks = structurifyChunkGenerator.structurify$getOverlapChecks();
		boolean overlapCheckResult = cachedOverlapChecks.computeIfAbsent(structureCheckId, id -> checkOverlap(structureCheckData, structurifyChunkGenerator));

		if (!overlapCheckResult) {
			return false;
		}

		return true;
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
		checkOverlap(structureCheckData, (StructurifyChunkGenerator) chunkGenerator);
	}

	private static boolean checkBiomes(
		StructureCheckData structureCheckData,
		BiomeSource biomeSource,
		RandomState randomState
	) {
		var structure = structureCheckData.getStructure();
		BiomeCheckData biomeCheckData = StructureBiomeCheck.getBiomeCheckData(structureCheckData);

		if (biomeCheckData == null || !biomeCheckData.isEnabled()) {
			return true;
		}

		if (biomeSource instanceof CheckerboardColumnBiomeSource) {
			return true;
		}

		var structureData = structure.structurify$getStructureData();

		if(structureData == null) {
			return true;
		}

		var biomeCheckResult = StructureBiomeCheck.checkBiomes(structureCheckData, biomeCheckData, biomeSource, randomState);

		if(!biomeCheckResult) {
			return false;
		}

		return true;
	}

	private static boolean checkFlatness(
		StructureCheckData structureCheckData,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState
	) {
		FlatnessCheckData flatnessCheckData = StructureFlatnessCheck.getFlatnessCheckData(structureCheckData);

		if(!StructureFlatnessCheck.canDoFlatnessCheck(structureCheckData)) {
			return true;
		}

		boolean flatnessCheckResult = StructureFlatnessCheck.checkFlatness(
			structureCheckData,
			flatnessCheckData,
			chunkGenerator,
			heightAccessor,
			randomState
		);

		if(!flatnessCheckResult) {
			return false;
		}

		return true;
	}

	private static boolean checkOverlap(
		StructureCheckData structureCheckData,
		StructurifyChunkGenerator chunkGenerator
	) {
		if (!Structurify.getConfig().preventStructureOverlap) {
			return true;
		}

		var structureData = structureCheckData.getStructure().structurify$getStructureData();

		if(structureData == null) {
			return true;
		}

		var structureStep = structureData.getStep();

		if (structureStep == GenerationStep.Decoration.RAW_GENERATION) {
			return true;
		}

		boolean overlapCheckResult = StructureOverlapCheck.checkForOverlap(structureCheckData, chunkGenerator);

		if (overlapCheckResult) {
			return false;
		}

		return true;
	}

	public static long generateStructureCheckId(ResourceLocation structureId, BlockPos structureCenter) {
		long structHash = structureId.hashCode();
		structHash ^= (structHash >>> 33);
		structHash *= 0xff51afd7ed558ccdL;
		structHash ^= (structHash >>> 33);
		structHash *= 0xc4ceb9fe1a85ec53L;
		structHash ^= (structHash >>> 33);

		return Long.rotateLeft(structHash, 23) ^ Long.rotateLeft(ChunkPos.asLong(structureCenter), 11);
	}
}
