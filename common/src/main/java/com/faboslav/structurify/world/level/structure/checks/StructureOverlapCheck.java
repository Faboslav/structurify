package com.faboslav.structurify.world.level.structure.checks;

import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.world.level.structure.StructureSectionClaim;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class StructureOverlapCheck
{
	public static boolean checkForOverlap(
		StructureCheckData structureCheckData,
		StructurifyChunkGenerator structurifyChunkGenerator
	) {
		long[] structureChunks = getStructurePiecesSections(structureCheckData.getStructureStart());

		if (!claimStructureSections(structurifyChunkGenerator, structureChunks, structureCheckData.getStructureId())) {
			return true;
		}

		return false;
	}

	private static long[] getStructurePiecesSections(StructureStart start) {
		Set<Long> structurePieceSectionUniqueKeys = new HashSet<>();

		for (var piece : start.getPieces()) {
			BoundingBox b = piece.getBoundingBox();

			int minSx = SectionPos.blockToSectionCoord(b.minX());
			int maxSx = SectionPos.blockToSectionCoord(b.maxX());
			int minSz = SectionPos.blockToSectionCoord(b.minZ());
			int maxSz = SectionPos.blockToSectionCoord(b.maxZ());
			int minSy = SectionPos.blockToSectionCoord(b.minY());
			int maxSy = SectionPos.blockToSectionCoord(b.maxY());

			for (int sz = minSz; sz <= maxSz; sz++) {
				for (int sx = minSx; sx <= maxSx; sx++) {
					for (int sy = minSy; sy <= maxSy; sy++) {
						structurePieceSectionUniqueKeys.add(SectionPos.asLong(sx, sy, sz));
					}
				}
			}
		}

		long[] structurePieceSectionKeys = new long[structurePieceSectionUniqueKeys.size()];
		int i = 0;

		for (Long structurePieceSectionKey : structurePieceSectionUniqueKeys) {
			structurePieceSectionKeys[i++] = structurePieceSectionKey;
		}

		return structurePieceSectionKeys;
	}

	private static boolean claimStructureSections(StructurifyChunkGenerator structurifyChunkGenerator, long[] sectionKeysToClaim, ResourceLocation structureId) {
		final long token = ThreadLocalRandom.current().nextLong();
		final long[] salted = new long[sectionKeysToClaim.length];

		int acquired = 0;
		for (; acquired < sectionKeysToClaim.length; acquired++) {
			long key = sectionKeysToClaim[acquired];
			salted[acquired] = key;
			StructureSectionClaim structureSectionClaim = new StructureSectionClaim(token, structureId);

			StructureSectionClaim prev = structurifyChunkGenerator.structurify$getStructureSectionClaims().putIfAbsent(key, structureSectionClaim);
			if (prev != null) {
				for (int i = 0; i < acquired; i++) {
					structurifyChunkGenerator.structurify$getStructureSectionClaims().remove(salted[i], structureSectionClaim);
				}
				return false;
			}
		}

		return true;
	}
}
