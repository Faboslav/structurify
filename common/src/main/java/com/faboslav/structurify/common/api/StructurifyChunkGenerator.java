package com.faboslav.structurify.common.api;

import com.faboslav.structurify.world.level.structure.StructureSectionClaim;

import java.util.Map;

public interface StructurifyChunkGenerator
{
	Map<Long, StructureSectionClaim> structurify$getStructureSectionClaims();
}
