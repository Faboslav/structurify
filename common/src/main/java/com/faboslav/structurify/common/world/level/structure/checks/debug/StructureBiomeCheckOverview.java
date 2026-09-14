package com.faboslav.structurify.common.world.level.structure.checks.debug;

import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.common.util.ChunkPosUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import java.util.List;

public record StructureBiomeCheckOverview(
	Identifier structureId,
	BoundingBox structureBoundingBox,
	List<StructurePiece> structurePieces,
	BiomeCheckData.BiomeCheckMode biomeCheckMode,
	int checks,
	boolean result,
	StructurePlacementAttempt structurePlacementAttempt
)
{
	@Override
	public String toString() {
		ChunkPos originalStructureChunk = structurePlacementAttempt.originalStructureChunk();

		return structureId + "\n" +
			   "Mode: " + biomeCheckMode + "\n" +
			   "Possible checks: " + checks + "\n" +
			   "Result: " + (result ? "success":"fail") + "\n" +
			   "Placement attempt: " + (structurePlacementAttempt.attempt() + 1) + "/" + structurePlacementAttempt.placementAttempts() + "\n" +
			   "Original chunk: " + ChunkPosUtil.getX(originalStructureChunk) + ", " + ChunkPosUtil.getZ(originalStructureChunk) + "\n";
	}
}