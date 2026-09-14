package com.faboslav.structurify.common.util;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.api.StructurifyStructureSet;
import com.faboslav.structurify.common.config.data.StructureSetData;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.jetbrains.annotations.Nullable;

public final class RandomSpreadUtil
{
	private static final int[][] PLACEMENT_ATTEMPT_DIRECTIONS = {
		{-1, 0}, {0, -1}, {1, 0}, {0, 1},
		{-1, -1}, {1, -1}, {1, 1}, {-1, 1}
	};

	private static StructureSetData getStructureSetData(String structureSetId) {
		if (structureSetId == null || !Structurify.getConfig().getStructureSetData().containsKey(structureSetId)) {
			return null;
		}

		return Structurify.getConfig().getStructureSetData().get(structureSetId);
	}

	public static int getModifiedSalt(String structureSetId, int originalSalt) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);

		if (structureSetData == null) {
			return originalSalt;
		}

		return structureSetData.getSalt();
	}

	public static float getModifiedFrequency(String structureSetId, float originalFrequency) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);

		if (structureSetData == null) {
			return originalFrequency;
		}

		return structureSetData.getFrequency();
	}

	public static int getModifiedSpacing(String structureSetId, int originalSpacing) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);
		return getModifiedSpacing(structureSetData, originalSpacing);
	}

	public static int getModifiedSpacing(StructureSetData structureSetData, int originalSpacing) {
		int spacing = originalSpacing;

		if (Structurify.getConfig().enableGlobalSpacingAndSeparationModifier) {
			if (structureSetData != null && structureSetData.overrideGlobalSpacingAndSeparationModifier()) {
				spacing = structureSetData.getSpacing();
			} else {
				spacing = (int) (spacing * Structurify.getConfig().globalSpacingAndSeparationModifier);
			}
		} else if (structureSetData != null) {
			spacing = structureSetData.getSpacing();
		}

		return getCorrectedModifiedSpacingValue(spacing);
	}

	public static int getModifiedSeparation(
		String structureSetId,
		int spacing,
		int originalSeparation
	) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);
		return getModifiedSeparation(structureSetData, spacing, originalSeparation);
	}

	public static int getModifiedSeparation(
		StructureSetData structureSetData,
		int spacing,
		int originalSeparation
	) {
		int separation = originalSeparation;

		if (Structurify.getConfig().enableGlobalSpacingAndSeparationModifier) {
			if (structureSetData != null && structureSetData.overrideGlobalSpacingAndSeparationModifier()) {
				separation = structureSetData.getSeparation();
			} else {
				separation = (int) (separation * Structurify.getConfig().globalSpacingAndSeparationModifier);
			}
		} else if (structureSetData != null) {
			separation = structureSetData.getSeparation();
		}

		return getCorrectedModifiedSeparationValue(spacing, separation);
	}

	private static int getCorrectedModifiedSpacingValue(int spacingValue) {
		return Math.max(1, spacingValue);
	}

	private static int getCorrectedModifiedSeparationValue(int spacing, int separation) {
		separation = Math.max(0, separation);

		if (separation >= spacing) {
			separation = spacing - 1;
		}

		return separation;
	}

	public static int getPlacementAttempts(String structureSetId) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);

		if (structureSetData == null) {
			return StructureSetData.PLACEMENT_ATTEMPTS_DEFAULT_VALUE;
		}

		return structureSetData.getPlacementAttempts();
	}

	public static int getPlacementAttemptChunkOffset(
		@Nullable StructureSet structureSet,
		int defaultChunkOffset
	) {
		if (structureSet == null) {
			return defaultChunkOffset;
		}

		int maxDistanceFromCenter = 0;

		for (var structureSelectionEntry : ((StructurifyStructureSet) (Object) structureSet).structurify$getOriginalStructures()) {
			if (!(structureSelectionEntry.structure().value() instanceof StructurifyStructure structurifyStructure)) {
				continue;
			}

			var structureData = structurifyStructure.structurify$getStructureData();

			if (structureData == null) {
				continue;
			}

			var horizontalMaxDistanceFromCenter = structureData.getJigsawData().getHorizontalMaxDistanceFromCenter();

			if (horizontalMaxDistanceFromCenter == null) {
				continue;
			}

			maxDistanceFromCenter = Math.max(maxDistanceFromCenter, horizontalMaxDistanceFromCenter);
		}

		if (maxDistanceFromCenter <= 0) {
			return defaultChunkOffset;
		}

		return Math.max(1, ChunkPosUtil.getChunkSpan(maxDistanceFromCenter));
	}

	public static ChunkPos getPlacementAttemptChunk(
		RandomSpreadStructurePlacement randomSpreadStructurePlacement,
		long levelSeed,
		int chunkX,
		int chunkZ,
		int attempt,
		int chunkOffset
	) {
		ChunkPos originalStructureChunk = randomSpreadStructurePlacement.getPotentialStructureChunk(levelSeed, chunkX, chunkZ);

		if (attempt <= 0) {
			return originalStructureChunk;
		}

		int spacing = randomSpreadStructurePlacement.spacing();
		int spreadBound = spacing - randomSpreadStructurePlacement.separation();
		int minChunkX = Math.floorDiv(chunkX, spacing) * spacing;
		int minChunkZ = Math.floorDiv(chunkZ, spacing) * spacing;

		int originalSpreadX = ChunkPosUtil.getX(originalStructureChunk) - minChunkX;
		int originalSpreadZ = ChunkPosUtil.getZ(originalStructureChunk) - minChunkZ;

		int placementAttemptChunkOffset = Math.min(
			Math.max(1, chunkOffset),
			Math.max(1, (spreadBound - 1) / 2)
		);

		int[] direction = PLACEMENT_ATTEMPT_DIRECTIONS[(attempt - 1) % PLACEMENT_ATTEMPT_DIRECTIONS.length];
		int attemptChunkOffset = ((attempt - 1) / PLACEMENT_ATTEMPT_DIRECTIONS.length + 1) * placementAttemptChunkOffset;

		int attemptSpreadX = getPlacementAttemptSpread(originalSpreadX, direction[0] * attemptChunkOffset, spreadBound);
		int attemptSpreadZ = getPlacementAttemptSpread(originalSpreadZ, direction[1] * attemptChunkOffset, spreadBound);

		return new ChunkPos(minChunkX + attemptSpreadX, minChunkZ + attemptSpreadZ);
	}

	private static int getPlacementAttemptSpread(int originalSpread, int chunkOffset, int spreadBound) {
		int attemptSpread = originalSpread + chunkOffset;

		if (attemptSpread < 0 || attemptSpread >= spreadBound) {
			attemptSpread = originalSpread - chunkOffset;
		}

		return Mth.clamp(attemptSpread, 0, spreadBound - 1);
	}

	public static boolean isWithinPlacementSpread(
		RandomSpreadStructurePlacement randomSpreadStructurePlacement,
		int chunkX,
		int chunkZ
	) {
		int spacing = randomSpreadStructurePlacement.spacing();
		int spreadBound = spacing - randomSpreadStructurePlacement.separation();

		int minChunkX = Math.floorDiv(chunkX, spacing) * spacing;
		int minChunkZ = Math.floorDiv(chunkZ, spacing) * spacing;

		return chunkX >= minChunkX
			   && chunkX < minChunkX + spreadBound
			   && chunkZ >= minChunkZ
			   && chunkZ < minChunkZ + spreadBound;
	}

	public static int getModifiedStructureWeight(String structureSetId, String structureId, int originalWeight) {
		StructureSetData structureSetData = getStructureSetData(structureSetId);

		if (structureSetData == null) {
			return originalWeight;
		}

		return Math.max(1, structureSetData.getStructureWeights().getOrDefault(structureId, originalWeight));
	}
}
