package com.faboslav.structurify.common.api;

import com.faboslav.structurify.common.world.level.structure.StructureSectionClaim;

import java.util.Map;

public interface StructurifyChunkGenerator
{
	Map<Long, StructureSectionClaim> structurify$getStructureSectionClaims();

	Map<Long, Boolean> structurify$getFlatnessChecks();

	Map<Long, Boolean> structurify$getBiomeChecks();

	Map<Long, Boolean> structurify$getOverlapChecks();
}
