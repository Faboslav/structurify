package com.faboslav.structurify.common.world.level.structure.checks;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.DebugData;
import com.faboslav.structurify.common.util.ChunkPosUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.*;

public final class StructurePieceSampler
{
	public static List<StructurePiece> getStructurePieces(StructureStart structureStart) {
		List<StructurePiece> structurePieces = structureStart.getPieces();

		if (Structurify.getConfig().getDebugData().isEnabled() && Structurify.getConfig().getDebugData().getSamplingMode() == DebugData.SamplingMode.MINIMAL) {
			return structurePieces;
		}

		structurePieces = getOnlyMainPieces(structurePieces);

		return structurePieces;
	}

	public static int[][] getStructurePieceSamples(List<StructurePiece> structurePieces, BlockPos structureCenter) {
		if (structurePieces.isEmpty()) {
			return new int[0][];
		}

		int[][] pieceSamples = getStructurePieceCorners(structurePieces);

		if (Structurify.getConfig().getDebugData().isEnabled() && Structurify.getConfig().getDebugData().getSamplingMode() == DebugData.SamplingMode.MINIMAL) {
			return pieceSamples;
		}

		pieceSamples = mergeTouchingPositions(pieceSamples);

		if (Structurify.getConfig().getDebugData().isEnabled() && Structurify.getConfig().getDebugData().getSamplingMode() == DebugData.SamplingMode.MERGED_SAMPLES) {
			return pieceSamples;
		}

		pieceSamples = orderStructureCornersByDistance(pieceSamples, structureCenter);

		return pieceSamples;
	}

	private static List<StructurePiece> getOnlyMainPieces(List<StructurePiece> pieces) {
		final int count = pieces.size();
		final List<StructurePiece> mainPieces = new ArrayList<>(count);

		for (int candidateIndex = 0; candidateIndex < count; candidateIndex++) {
			final StructurePiece candidatePiece = pieces.get(candidateIndex);
			final BoundingBox candidateBox = candidatePiece.getBoundingBox();

			if (candidateBox.getXSpan() <= 2 || candidateBox.getZSpan() <= 2) {
				continue;
			}

			boolean isInsideAnotherPiece = false;
			for (int otherIndex = 0; otherIndex < count; otherIndex++) {
				if (candidateIndex == otherIndex) continue;
				if (containsBox(pieces.get(otherIndex).getBoundingBox(), candidateBox)) {
					isInsideAnotherPiece = true;
					break;
				}
			}

			if (!isInsideAnotherPiece) {
				mainPieces.add(candidatePiece);
			}
		}

		return mainPieces;
	}

	private static int[][] getStructurePieceCorners(List<StructurePiece> pieces) {
		int pieceCount = pieces.size();
		int[][] structureCorners = new int[pieceCount * 5][2];
		int structureCornerIndex = 0;

		for (StructurePiece piece : pieces) {
			BoundingBox pieceBox = piece.getBoundingBox();

			int minX = pieceBox.minX();
			int minZ = pieceBox.minZ();
			int maxX = pieceBox.maxX();
			int maxZ = pieceBox.maxZ();

			structureCorners[structureCornerIndex++] = new int[]{minX, minZ};
			structureCorners[structureCornerIndex++] = new int[]{minX, maxZ};
			structureCorners[structureCornerIndex++] = new int[]{maxX, minZ};
			structureCorners[structureCornerIndex++] = new int[]{maxX, maxZ};

			int centerX = (minX + maxX) / 2;
			int centerZ = (minZ + maxZ) / 2;
			structureCorners[structureCornerIndex++] = new int[]{centerX, centerZ};
		}

		return structureCorners;
	}

	private static int[][] mergeTouchingPositions(int[][] inputPositions) {
		Set<Long> uniqueKeys = new HashSet<>();
		List<int[]> uniquePositions = new ArrayList<>();

		for (int[] position : inputPositions) {
			long key = (((long) position[0]) << 32) ^ (position[1] & 0xFFFFFFFFL);

			if (uniqueKeys.add(key)) {
				uniquePositions.add(new int[] { position[0], position[1] });
			}
		}

		List<int[]> mergedPositions = new ArrayList<>();
		boolean[] visited = new boolean[uniquePositions.size()];

		for (int i = 0; i < uniquePositions.size(); i++) {
			if (visited[i]) {
				continue;
			}

			List<int[]> group = new ArrayList<>();
			Queue<Integer> queue = new ArrayDeque<>();
			queue.add(i);
			visited[i] = true;

			while (!queue.isEmpty()) {
				int currentIndex = queue.poll();
				int[] current = uniquePositions.get(currentIndex);
				group.add(current);

				for (int j = 0; j < uniquePositions.size(); j++) {
					if (visited[j]) {
						continue;
					}

					int[] candidate = uniquePositions.get(j);

					if (Math.abs(candidate[0] - current[0]) <= 1 && Math.abs(candidate[1] - current[1]) <= 1) {
						visited[j] = true;
						queue.add(j);
					}
				}
			}

			int minX = Integer.MAX_VALUE;
			int minZ = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int maxZ = Integer.MIN_VALUE;

			for (int[] position : group) {
				minX = Math.min(minX, position[0]);
				minZ = Math.min(minZ, position[1]);
				maxX = Math.max(maxX, position[0]);
				maxZ = Math.max(maxZ, position[1]);
			}

			int centerX = Math.floorDiv(minX + maxX, 2);
			int centerZ = Math.floorDiv(minZ + maxZ, 2);

			int[] best = group.get(0);
			long bestDistance = Long.MAX_VALUE;

			for (int[] position : group) {
				long dx = position[0] - centerX;
				long dz = position[1] - centerZ;
				long distance = dx * dx + dz * dz;

				if (distance < bestDistance || distance == bestDistance && (position[0] < best[0] || position[0] == best[0] && position[1] < best[1])) {
					best = position;
					bestDistance = distance;
				}
			}

			mergedPositions.add(new int[] { best[0], best[1] });
		}

		return mergedPositions.toArray(new int[0][]);
	}

	private static int[][] orderStructureCornersByDistance(int[][] samples, BlockPos structureCenter) {
		final int centerX = structureCenter.getX();
		final int centerZ = structureCenter.getZ();

		List<int[]> sortedSamples = new ArrayList<>(samples.length);
		sortedSamples.addAll(Arrays.asList(samples));

		sortedSamples.sort((a, b) -> {
			int ax = a[0], az = a[1];
			int bx = b[0], bz = b[1];

			int distA = Math.max(Math.abs(ax - centerX), Math.abs(az - centerZ));
			int distB = Math.max(Math.abs(bx - centerX), Math.abs(bz - centerZ));

			return Integer.compare(distB, distA);
		});

		int[][] result = new int[sortedSamples.size()][2];
		for (int i = 0; i < sortedSamples.size(); i++) {
			result[i] = sortedSamples.get(i);
		}
		return result;
	}


	private static boolean containsBox(BoundingBox outer, BoundingBox inner) {
		return outer.minX() <= inner.minX() && outer.maxX() >= inner.maxX()
			   && outer.minY() <= inner.minY() && outer.maxY() >= inner.maxY()
			   && outer.minZ() <= inner.minZ() && outer.maxZ() >= inner.maxZ();
	}

	private StructurePieceSampler() {
	}
}