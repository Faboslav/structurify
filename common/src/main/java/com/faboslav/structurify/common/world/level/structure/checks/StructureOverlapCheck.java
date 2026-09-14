package com.faboslav.structurify.common.world.level.structure.checks;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.config.data.structure.OverlapCheckData;
import com.faboslav.structurify.common.world.level.structure.StructureSectionClaim;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.Nullable;

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

		OverlapCheckData structureOverlapCheckData = structureData != null ? structureData.getOverlapCheckData() : null;
		OverlapCheckData namespaceOverlapCheckData = structureNamespaceData != null ? structureNamespaceData.getOverlapCheckData() : null;

		if (structureOverlapCheckData != null && structureOverlapCheckData.isExcludedFromOverlapPrevention()) {
			return structureOverlapCheckData;
		}

		if (namespaceOverlapCheckData != null && namespaceOverlapCheckData.isExcludedFromOverlapPrevention()) {
			return namespaceOverlapCheckData;
		}

		if (structureOverlapCheckData != null) {
			return structureOverlapCheckData;
		}

		return namespaceOverlapCheckData;
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
		@Nullable OverlapCheckData overlapCheckData,
		StructurifyChunkGenerator structurifyChunkGenerator
	) {
		return checkForOverlap(structureCheckData, overlapCheckData, structurifyChunkGenerator, false);
	}

	public static boolean checkForOverlap(
		StructureCheckData structureCheckData,
		@Nullable OverlapCheckData overlapCheckData,
		StructurifyChunkGenerator structurifyChunkGenerator,
		boolean testOnly
	) {
		StructureStart start = structureCheckData.getStructureStart();

		long[] structureCells = getStructurePiecesSections(start, resolveOverlapPadding(overlapCheckData));

		long structureCenter = structureCheckData.getStructureCenter().asLong();

		if (testOnly) {
			return !canClaimStructureSections(
				structurifyChunkGenerator,
				structureCells,
				structureCheckData.getStructureId(),
				structureCenter
			);
		}

		boolean overlapCheckResult = !claimStructureSections(
			structurifyChunkGenerator,
			structureCells,
			structureCheckData.getStructureId(),
			structureCenter
		);

		return overlapCheckResult;
	}

	public static void releaseStructureSections(
		StructureCheckData structureCheckData,
		@Nullable OverlapCheckData overlapCheckData,
		StructurifyChunkGenerator structurifyChunkGenerator
	) {
		long[] structureCells = getStructurePiecesSections(structureCheckData.getStructureStart(), resolveOverlapPadding(overlapCheckData));

		String structureId = structureCheckData.getStructureId().toString();
		long structureCenter = structureCheckData.getStructureCenter().asLong();

		for (long structureCell : structureCells) {
			StructureSectionClaim claim = structurifyChunkGenerator.structurify$getStructureSectionClaims().get(structureCell);

			if (claim != null && structureId.equals(claim.structureId()) && structureCenter == claim.structureCenter()) {
				structurifyChunkGenerator.structurify$getStructureSectionClaims().remove(structureCell, claim);
			}
		}
	}

	private static int resolveOverlapPadding(@Nullable OverlapCheckData overlapCheckData) {
		if (overlapCheckData != null && overlapCheckData.isOverridingGlobalOverlapPadding()) {
			return overlapCheckData.getOverlapPadding();
		}

		return Structurify.getConfig().overlapPadding;
	}

	private static long[] getStructurePiecesSections(StructureStart start, int padding) {
		Set<Long> structurePieceSectionUniqueKeys = new LongOpenHashSet();

		for (var piece : start.getPieces()) {
			BoundingBox b = piece.getBoundingBox();

			int minSx = SectionPos.blockToSectionCoord(b.minX()) - padding;
			int maxSx = SectionPos.blockToSectionCoord(b.maxX()) + padding;
			int minSz = SectionPos.blockToSectionCoord(b.minZ()) - padding;
			int maxSz = SectionPos.blockToSectionCoord(b.maxZ()) + padding;

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

		for (long structurePieceSectionKey : structurePieceSectionUniqueKeys) {
			structurePieceSectionKeys[i++] = structurePieceSectionKey;
		}

		return structurePieceSectionKeys;
	}

	private static boolean canClaimStructureSections(
		StructurifyChunkGenerator gen,
		long[] sectionKeysToClaim,
		Identifier structureId,
		long structureCenter
	) {
		for (long key : sectionKeysToClaim) {
			StructureSectionClaim prev = gen.structurify$getStructureSectionClaims().get(key);

			if (prev == null) {
				continue;
			}

			if (structureId.toString().equals(prev.structureId()) && structureCenter == prev.structureCenter()) {
				continue;
			}

			return false;
		}

		return true;
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