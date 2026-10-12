package com.faboslav.structurify.common.api;

import com.faboslav.structurify.common.world.level.structure.StructureChunkResolution;
import com.faboslav.structurify.common.world.level.structure.StructureSectionClaim;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface StructurifyChunkGenerator
{
	Map<Long, StructureSectionClaim> structurify$getStructureSectionClaims();

	Map<Long, StructureChunkResolution> structurify$getResolvedStructureChunks();

	Map<Long, CompletableFuture<Boolean>> structurify$getStructureChecks();
}
