package com.faboslav.structurify.common.world.level.structure.checks;

import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.faboslav.structurify.common.config.data.structure.OverlapCheckData;
import com.faboslav.structurify.common.util.ChunkPosUtil;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructurePlacementAttempt;
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

		if(structureStart.getPieces().isEmpty()) {
			return false;
		}

		long structureCheckId = generateStructureCheckId(structureId, structureStart.getChunkPos());
		StructurifyChunkGenerator structurifyChunkGenerator = (StructurifyChunkGenerator) chunkGenerator;

		@Nullable Identifier finalStructureId = structureId;

		return structurifyChunkGenerator.structurify$getStructureChecks().computeIfAbsent(structureCheckId, id -> {
			StructureCheckData structureCheckData = new StructureCheckData(structureCheckId, finalStructureId, structure, structureStart, StructurePlacementAttempt.of(structureStart.getChunkPos()));

			boolean overlapCheckResult = structurifyChunkGenerator.structurify$getOverlapChecks().computeIfAbsent(structureCheckId, id2 -> checkOverlap(structureCheckData, structurifyChunkGenerator, false));

			if (!overlapCheckResult) {
				return false;
			}

			boolean biomeCheckResult = structurifyChunkGenerator.structurify$getBiomeChecks().computeIfAbsent(structureCheckId, id2 -> checkBiomes(structureCheckData, biomeSource, randomState));

			if (!biomeCheckResult) {
				releaseOverlapClaims(structureCheckData, structurifyChunkGenerator);
				return false;
			}

			boolean flatnessCheckResult = structurifyChunkGenerator.structurify$getFlatnessChecks().computeIfAbsent(structureCheckId, id2 -> checkFlatness(structureCheckData, chunkGenerator, heightAccessor, randomState));

			if (!flatnessCheckResult) {
				releaseOverlapClaims(structureCheckData, structurifyChunkGenerator);
				return false;
			}

			return true;
		});
	}

	public static boolean testStructure(
		StructureStart structureStart,
		StructurifyStructure structure,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		BiomeSource biomeSource,
		StructurePlacementAttempt structurePlacementAttempt
	) {
		return testStructure(structureStart, structure, chunkGenerator, heightAccessor, randomState, biomeSource, structurePlacementAttempt, false);
	}

	private static boolean testStructure(
		StructureStart structureStart,
		StructurifyStructure structure,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		BiomeSource biomeSource,
		StructurePlacementAttempt structurePlacementAttempt,
		boolean runAllChecks
	) {
		if (structureStart == StructureStart.INVALID_START || structureStart.getPieces().isEmpty()) {
			return false;
		}

		Identifier structureId = structure.structurify$getStructureIdentifier();

		if (structureId == null) {
			return true;
		}

		long structureCheckId = generateStructureCheckId(structureId, structureStart.getChunkPos());
		StructureCheckData structureCheckData = new StructureCheckData(structureCheckId, structureId, structure, structureStart, structurePlacementAttempt);

		boolean overlapCheckResult = checkOverlap(structureCheckData, (StructurifyChunkGenerator) chunkGenerator, true);

		if (!overlapCheckResult && !runAllChecks) {
			return false;
		}

		boolean biomeCheckResult = checkBiomes(structureCheckData, biomeSource, randomState);

		if (!biomeCheckResult && !runAllChecks) {
			return false;
		}

		boolean flatnessCheckResult = checkFlatness(structureCheckData, chunkGenerator, heightAccessor, randomState);

		return overlapCheckResult && biomeCheckResult && flatnessCheckResult;
	}

	private static void releaseOverlapClaims(
		StructureCheckData structureCheckData,
		StructurifyChunkGenerator structurifyChunkGenerator
	) {
		OverlapCheckData overlapCheckData = StructureOverlapCheck.getOverlapCheckData(structureCheckData);

		if (!StructureOverlapCheck.canDoOverlapCheck(structureCheckData, overlapCheckData)) {
			return;
		}

		StructureOverlapCheck.releaseStructureSections(structureCheckData, overlapCheckData, structurifyChunkGenerator);
	}

	public static void debugCheckStructure(
		StructureStart structureStart,
		StructurifyStructure structure,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		BiomeSource biomeSource,
		StructurePlacementAttempt structurePlacementAttempt
	) {
		testStructure(structureStart, structure, chunkGenerator, heightAccessor, randomState, biomeSource, structurePlacementAttempt, true);
	}

	private static boolean checkBiomes(
		StructureCheckData structureCheckData,
		BiomeSource biomeSource,
		RandomState randomState
	) {
		try {
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
		} catch (Throwable e) {
			return true;
		}
	}

	private static boolean checkFlatness(
		StructureCheckData structureCheckData,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState
	) {
		try {
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
		} catch (Throwable e) {
			return true;
		}
	}

	private static boolean checkOverlap(
		StructureCheckData structureCheckData,
		StructurifyChunkGenerator chunkGenerator,
		boolean testOnly
	) {
		try {
			OverlapCheckData overlapCheckData = StructureOverlapCheck.getOverlapCheckData(structureCheckData);

			if (!StructureOverlapCheck.canDoOverlapCheck(structureCheckData, overlapCheckData)) {
				return true;
			}

			boolean overlapCheckResult = StructureOverlapCheck.checkForOverlap(structureCheckData, overlapCheckData, chunkGenerator, testOnly);

			if (overlapCheckResult) {
				return false;
			}

			return true;
		} catch (Throwable e) {
			return true;
		}
	}

	public static long generateStructureCheckId(Identifier structureId, ChunkPos chunkPos) {
		long structureHash = structureId.hashCode() & 0xffffffffL;
		long chunkHash = ChunkPosUtil.getChunkPosAsLong(chunkPos);

		long structureCheckId = structureHash * 0x9E3779B97F4A7C15L + chunkHash;
		structureCheckId = (structureCheckId ^ (structureCheckId >>> 30)) * 0xBF58476D1CE4E5B9L;
		structureCheckId = (structureCheckId ^ (structureCheckId >>> 27)) * 0x94D049BB133111EBL;

		return structureCheckId ^ (structureCheckId >>> 31);
	}
}
