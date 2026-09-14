package com.faboslav.structurify.common.world.level.structure.checks.debug;

import com.faboslav.structurify.common.config.data.StructureSetData;
import net.minecraft.world.level.ChunkPos;

public record StructurePlacementAttempt(
	int attempt,
	int placementAttempts,
	ChunkPos originalStructureChunk
)
{
	public static StructurePlacementAttempt of(ChunkPos structureChunk) {
		return new StructurePlacementAttempt(0, StructureSetData.PLACEMENT_ATTEMPTS_DEFAULT_VALUE, structureChunk);
	}
}
