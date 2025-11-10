package com.faboslav.structurify.world.level.structure.checks;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.world.level.structure.StructureSectionClaim;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
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
		Set<Long> unique = new HashSet<>();

		for (StructurePiece piece : start.getPieces()) {
			BoundingBox box = piece.getBoundingBox();

			int minCx = Math.floorDiv(box.minX(), CELL_X);
			int maxCx = Math.floorDiv(box.maxX(), CELL_X);
			int minCy = Math.floorDiv(box.minY(), CELL_Y);
			int maxCy = Math.floorDiv(box.maxY(), CELL_Y);
			int minCz = Math.floorDiv(box.minZ(), CELL_Z);
			int maxCz = Math.floorDiv(box.maxZ(), CELL_Z);

			for (int cz = minCz; cz <= maxCz; cz++) {
				for (int cx = minCx; cx <= maxCx; cx++) {
					for (int cy = minCy; cy <= maxCy; cy++) {
						unique.add(packCell(cx, cy, cz));
					}
				}
			}
		}

		long[] keys = new long[unique.size()];
		int i = 0;
		for (Long k : unique) {
			keys[i++] = k;
		}
		return keys;
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
		StructurifyChunkGenerator gen,
		long[] sectionKeysToClaim,
		ResourceLocation structureId,
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