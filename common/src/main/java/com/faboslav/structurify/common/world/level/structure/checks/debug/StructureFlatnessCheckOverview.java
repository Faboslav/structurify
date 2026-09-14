package com.faboslav.structurify.common.world.level.structure.checks.debug;

import com.faboslav.structurify.common.util.ChunkPosUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import java.util.List;

public record StructureFlatnessCheckOverview(
	Identifier structureId,
	BoundingBox structureBoundingBox,
	List<StructurePiece> structurePieces,
	int structureArea,
	int minHeight,
	int maxHeight,
	int flatnessCheckThreshold,
	int totalFlatnessChecks,
	int failedNonSolidChecks,
	int nonSolidFlatnessChecksThreshold,
	boolean result,
	StructurePlacementAttempt structurePlacementAttempt
)
{
	@Override
	public String toString() {
		ChunkPos originalStructureChunk = structurePlacementAttempt.originalStructureChunk();

		return structureId + "\n" +
			   "Pieces: " + structurePieces.size() + " (area: " + structureArea + ")\n" +
			   "Height threshold: " + flatnessCheckThreshold + " (min Y: " + minHeight + ", max Y: " + maxHeight + ", diff: " + (maxHeight - minHeight) +")\n" +
			   "Total checks: " + totalFlatnessChecks + "\n" +
			   "Failed checks: " + failedNonSolidChecks + "/" + nonSolidFlatnessChecksThreshold + "\n" +
			   "Result: " + (result ? "success":"fail") + "\n" +
			   "Placement attempt: " + (structurePlacementAttempt.attempt() + 1) + "/" + structurePlacementAttempt.placementAttempts() + "\n" +
			   "Original chunk: " + ChunkPosUtil.getX(originalStructureChunk) + ", " + ChunkPosUtil.getZ(originalStructureChunk) + "\n";
	}
}