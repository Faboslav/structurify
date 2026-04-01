package com.faboslav.structurify.common.world.level.structure.checks;

import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.faboslav.structurify.common.config.data.structure.OverlapCheckData;
import com.faboslav.structurify.common.util.ChunkPosUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.Nullable;

public final class StructureChecker
{
	public static boolean checkStructure(
		StructureStart structureStart,
		@Nullable Identifier structureId,
		StructurifyStructure structure,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		BiomeSource biomeSource
	) {
		if(structureStart == StructureStart.INVALID_START) {
			return true;
		}

		if(structureId == null) {
			structureId = structure.structurify$getStructureIdentifier();
		}

		if(structureId == null) {
			return true;
		}

		long structureCheckId = generateStructureCheckId(structureId, structureStart.getChunkPos());
		StructurifyChunkGenerator structurifyChunkGenerator = (StructurifyChunkGenerator) chunkGenerator;

		@Nullable Identifier finalStructureId = structureId;
		return structurifyChunkGenerator.structurify$getStructureChecks().computeIfAbsent(structureCheckId, id -> {
			StructureCheckData structureCheckData = new StructureCheckData(structureCheckId, finalStructureId, structure, structureStart);

			boolean biomeCheckResult = structurifyChunkGenerator.structurify$getBiomeChecks().computeIfAbsent(structureCheckId, id2 -> checkBiomes(structureCheckData, biomeSource, randomState));

			if (!biomeCheckResult) {
				return false;
			}

			boolean flatnessCheckResult = structurifyChunkGenerator.structurify$getFlatnessChecks().computeIfAbsent(structureCheckId, id2 -> checkFlatness(structureCheckData, chunkGenerator, heightAccessor, randomState));
			if (!flatnessCheckResult) {
				return false;
			}

			boolean overlapCheckResult = structurifyChunkGenerator.structurify$getOverlapChecks().computeIfAbsent(structureCheckId, id2 -> checkOverlap(structureCheckData, structurifyChunkGenerator));

			if (!overlapCheckResult) {
				return false;
			}

			return true;
		});
	}

	public static void debugCheckStructure(
		StructureStart structureStart,
		StructurifyStructure structure,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		BiomeSource biomeSource
	) {
		if(structureStart == StructureStart.INVALID_START) {
			return;
		}

		Identifier structureId = structure.structurify$getStructureIdentifier();

		if(structureId == null) {
			return;
		}

		long structureCheckId = generateStructureCheckId(structureId, structureStart.getChunkPos());
		StructureCheckData structureCheckData = new StructureCheckData(structureCheckId, structureId, structure, structureStart);

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

		if(!StructureFlatnessCheck.canDoFlatnessCheck(structureCheckData, flatnessCheckData)) {
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
		OverlapCheckData overlapCheckData = StructureOverlapCheck.getOverlapCheckData(structureCheckData);

		if(!StructureOverlapCheck.canDoOverlapCheck(structureCheckData, overlapCheckData)) {
			return true;
		}

		boolean overlapCheckResult = StructureOverlapCheck.checkForOverlap(structureCheckData, overlapCheckData, chunkGenerator);

		if (overlapCheckResult) {
			return false;
		}

		return true;
	}

	public static long generateStructureCheckId(Identifier structureId, ChunkPos chunkPos) {
		long structureHash = structureId.hashCode() & 0xffffffffL;
		long chunkHash = ChunkPosUtil.getChunkPosAsLong(chunkPos);

		return (structureHash << 32) ^ chunkHash;
	}
}
