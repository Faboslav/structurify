package com.faboslav.structurify.world.level.structure.checks;

import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.world.level.structure.StructureSectionClaim;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class StructureOverlapCheck
{
	public static final int CELL_X = 8;
	public static final int CELL_Y = 8;
	public static final int CELL_Z = 8;

	public static boolean checkForOverlap(
		StructureCheckData structureCheckData,
		StructurifyChunkGenerator structurifyChunkGenerator
	) {
		long[] structureCells = getStructurePiecesSections(structureCheckData.getStructureStart());
		return !claimStructureSections(structurifyChunkGenerator, structureCells, structureCheckData.getStructureId());
	}

	private static long[] getStructurePiecesSections(StructureStart start) {
		Set<Long> structurePieceSectionUniqueKeys = new HashSet<>();

		for (var piece : start.getPieces()) {
			BoundingBox b = piece.getBoundingBox();

			int minCx = Math.floorDiv(b.minX(), CELL_X);
			int maxCx = Math.floorDiv(b.maxX(), CELL_X);
			int minCy = Math.floorDiv(b.minY(), CELL_Y);
			int maxCy = Math.floorDiv(b.maxY(), CELL_Y);
			int minCz = Math.floorDiv(b.minZ(), CELL_Z);
			int maxCz = Math.floorDiv(b.maxZ(), CELL_Z);

			for (int cz = minCz; cz <= maxCz; cz++) {
				for (int cx = minCx; cx <= maxCx; cx++) {
					for (int cy = minCy; cy <= maxCy; cy++) {
						structurePieceSectionUniqueKeys.add(packCell(cx, cy, cz));
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

	private static long packCell(int x, int y, int z) {
		return ((long)(x & 0x1FFFFF) << 42)
			   | ((long)(y & 0x1FFFFF) << 21)
			   | (long)(z & 0x1FFFFF);
	}

	public static BlockPos unpackCell(long packedKey, int cellX, int cellY, int cellZ) {
		int x = (int) (packedKey >> 42);
		int y = (int) ((packedKey >> 21) & 0x1FFFFF);
		int z = (int) (packedKey & 0x1FFFFF);

		if (x >= 0x100000) x -= 0x200000;
		if (y >= 0x100000) y -= 0x200000;
		if (z >= 0x100000) z -= 0x200000;

		return new BlockPos(x * cellX, y * cellY, z * cellZ);
	}

	private static boolean claimStructureSections(
		StructurifyChunkGenerator structurifyChunkGenerator,
		long[] sectionKeysToClaim,
		ResourceLocation structureId
	) {
		final long token = ThreadLocalRandom.current().nextLong();
		final long[] salted = new long[sectionKeysToClaim.length];

		int acquired = 0;
		for (; acquired < sectionKeysToClaim.length; acquired++) {
			long key = sectionKeysToClaim[acquired];
			salted[acquired] = key;
			StructureSectionClaim structureSectionClaim = new StructureSectionClaim(token, structureId.toString(), "");

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