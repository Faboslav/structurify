package com.faboslav.structurify.world.level.structure.checks;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.config.data.structure.OverlapCheckData;
import com.faboslav.structurify.world.level.structure.StructureSectionClaim;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class StructureOverlapCheck
{
	@Nullable
	public static OverlapCheckData getOverlapCheckData(
		StructureCheckData structureCheckData
	) {
		var structure = structureCheckData.getStructure();
		var structureId = structureCheckData.getStructureId();
		var structureNamespaceData = structure.structurify$getStructureNamespaceData(structureId);
		var structureData = structure.structurify$getStructureData(structureId);

		@Nullable
		OverlapCheckData overlapCheckDataToCheck = null;

		if(structureNamespaceData != null) {
			var namespaceOverlapCheckData = structureNamespaceData.getOverlapCheckData();

			if (namespaceOverlapCheckData.isExcludedFromOverlapPrevention()) {
				overlapCheckDataToCheck = namespaceOverlapCheckData;
			}
		}

		if(structureData != null) {
			var structureOverlapCheckData = structureData.getOverlapCheckData();

			if (structureOverlapCheckData.isExcludedFromOverlapPrevention()) {
				overlapCheckDataToCheck = structureOverlapCheckData;
			}
		}

		return overlapCheckDataToCheck;
	}

	public static boolean canDoOverlapCheck(
		StructureCheckData structureCheckData,
		@Nullable OverlapCheckData overlapCheckData
	) {
		if (!Structurify.getConfig().preventStructureOverlap) {
			return false;
		}

		var structureData = structureCheckData.getStructure().structurify$getStructureData();

		if(structureData == null) {
			return false;
		}

		if(overlapCheckData != null && overlapCheckData.isExcludedFromOverlapPrevention()) {
			return false;
		}

		return true;
	}

	public static boolean checkForOverlap(
		StructureCheckData structureCheckData,
		OverlapCheckData overlapCheckData,
		StructurifyChunkGenerator structurifyChunkGenerator
	) {
		StructureStart start = structureCheckData.getStructureStart();
		long[] structureCells = getStructurePiecesSections(start);

		long structureCenter = start.getBoundingBox().getCenter().asLong();

		boolean overlapCheckResult = !claimStructureSections(
			structurifyChunkGenerator,
			structureCells,
			structureCheckData.getStructureId(),
			structureCenter
		);

		return overlapCheckResult;
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

	private static boolean claimStructureSections(
		StructurifyChunkGenerator gen,
		long[] sectionKeysToClaim,
		Identifier structureId,
		long structureCenter
	) {
		final long token = ThreadLocalRandom.current().nextLong();
		final StructureSectionClaim claim = new StructureSectionClaim(token, structureId.toString(), structureCenter);
		final long[] claimed = new long[sectionKeysToClaim.length];

		int acquired = 0;
		for (; acquired < sectionKeysToClaim.length; acquired++) {
			long key = sectionKeysToClaim[acquired];
			claimed[acquired] = key;

			StructureSectionClaim prev = gen.structurify$getStructureSectionClaims().putIfAbsent(key, claim);
			if (prev != null) {
				if (structureId.toString().equals(prev.structureId()) && structureCenter == prev.structureCenter()) {
					continue;
				}

				for (int i = 0; i < acquired; i++) {
					gen.structurify$getStructureSectionClaims().remove(claimed[i], claim);
				}

				return false;
			}
		}

		return true;
	}
}