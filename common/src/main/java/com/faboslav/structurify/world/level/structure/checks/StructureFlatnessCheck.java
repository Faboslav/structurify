package com.faboslav.structurify.world.level.structure.checks;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureFlatnessCheckOverview;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureFlatnessCheckSample;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.HashSet;
import java.util.Set;

public final class StructureFlatnessCheck
{
	public static FlatnessCheckData getFlatnessCheckData(
		StructurifyStructure structure
	) {
		var globalFlatnessCheckData = structure.structurify$getGlobalStructureNamespaceData().getFlatnessCheckData();
		var namespaceFlatnessCheckData = structure.structurify$getStructureNamespaceData().getFlatnessCheckData();
		var structureFlatnessCheckData = structure.structurify$getStructureData().getFlatnessCheckData();

		FlatnessCheckData flatnessCheckDataToCheck = globalFlatnessCheckData;

		if(namespaceFlatnessCheckData.isOverridingGlobalFlatnessCheck()) {
			flatnessCheckDataToCheck = namespaceFlatnessCheckData;
		}

		if(structureFlatnessCheckData.isOverridingGlobalFlatnessCheck()) {
			flatnessCheckDataToCheck = structureFlatnessCheckData;
		}

		return flatnessCheckDataToCheck;
	}

	public static boolean checkFlatness(
		StructureCheckData structureCheckData,
		FlatnessCheckData flatnessCheckData,
		ChunkGenerator chunkGenerator,
		LevelHeightAccessor heightAccessor,
		RandomState randomState
	) {
		var structurePieces = structureCheckData.getStructurePieces();
		var structurePieceSamples = structureCheckData.getStructurePieceSamples();
		var structureId = structureCheckData.getStructureId();
		var structureStart = structureCheckData.getStructureStart();
		var structureCenter = structureCheckData.getStructureCenter();

		Set<StructureFlatnessCheckSample> flatnessCheckSamples = new HashSet<>();
		int structureArea = 0;

		for(var structurePiece : structurePieces) {
			BoundingBox structurePieceBoundingBox = structurePiece.getBoundingBox();
			int spanX = structurePieceBoundingBox.getXSpan();
			int spanZ = structurePieceBoundingBox.getZSpan();
			structureArea += spanX * spanZ;
		}

		int flatnessCheckHeightThreshold = net.minecraft.util.Mth.clamp(
			(int) Math.round(Math.sqrt(structureArea) * 0.25),
			4,
			32
		);

		int totalFlatnessChecks = structurePieceSamples.length;
		int nonSolidFlatnessChecks = 0;
		int nonSolidFlatnessChecksThreshold = Math.max(1, (int) Math.floor(totalFlatnessChecks * 0.33 * Math.pow(Math.min(1.0, (double) totalFlatnessChecks / 100.0), 0.33)));
		int minHeight = Integer.MAX_VALUE;
		int maxHeight = Integer.MIN_VALUE;

		for (int currentFlatnessCheck = 0; currentFlatnessCheck < totalFlatnessChecks; currentFlatnessCheck++) {
			int x = structurePieceSamples[currentFlatnessCheck][0];
			int z = structurePieceSamples[currentFlatnessCheck][1];

			int firstOceanFloorOccupiedHeight = chunkGenerator.getFirstOccupiedHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, heightAccessor, randomState);

			if (firstOceanFloorOccupiedHeight > maxHeight) {
				maxHeight = firstOceanFloorOccupiedHeight;
				if (maxHeight - minHeight > flatnessCheckHeightThreshold) {
					Structurify.getDebugRenderer().addStructureFlatnessCheckInfo(ChunkPos.asLong(structureCenter), new StructureFlatnessCheckOverview(structureId, structureStart.getBoundingBox(), structurePieces, structureArea, minHeight, maxHeight, flatnessCheckHeightThreshold, totalFlatnessChecks, nonSolidFlatnessChecks, nonSolidFlatnessChecksThreshold, false));
					return false;
				}
			}

			if (firstOceanFloorOccupiedHeight < minHeight) {
				minHeight = firstOceanFloorOccupiedHeight;
				if (maxHeight - minHeight > flatnessCheckHeightThreshold) {
					Structurify.getDebugRenderer().addStructureFlatnessCheckInfo(ChunkPos.asLong(structureCenter), new StructureFlatnessCheckOverview(structureId, structureStart.getBoundingBox(), structurePieces, structureArea, minHeight, maxHeight, flatnessCheckHeightThreshold, totalFlatnessChecks, nonSolidFlatnessChecks, nonSolidFlatnessChecksThreshold, false));
					return false;
				}
			}

			if (!flatnessCheckData.areNonSolidBlocksAllowed()) {
				int remainingFlatnessChecks = totalFlatnessChecks - currentFlatnessCheck;

				if ((nonSolidFlatnessChecks + remainingFlatnessChecks >= nonSolidFlatnessChecksThreshold) || Structurify.getConfig().getDebugData().isEnabled()) {
					int firstWorldSurfaceFreeHeight = chunkGenerator.getFirstFreeHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
					boolean isSolid = firstWorldSurfaceFreeHeight - firstOceanFloorOccupiedHeight == 1;

					if(Structurify.getConfig().getDebugData().isEnabled()) {
						flatnessCheckSamples.add(new StructureFlatnessCheckSample(structureId, x, z, firstOceanFloorOccupiedHeight, firstWorldSurfaceFreeHeight, isSolid));
					}

					if (!isSolid) {
						nonSolidFlatnessChecks++;

						if (nonSolidFlatnessChecks >= nonSolidFlatnessChecksThreshold) {
							Structurify.getDebugRenderer().addStructureFlatnessCheckInfo(ChunkPos.asLong(structureCenter), new StructureFlatnessCheckOverview(structureId, structureStart.getBoundingBox(), structurePieces, structureArea, minHeight, maxHeight, flatnessCheckHeightThreshold, totalFlatnessChecks, nonSolidFlatnessChecks, nonSolidFlatnessChecksThreshold, false));
							return false;
						}
					}
				}
			} else {
				flatnessCheckSamples.add(new StructureFlatnessCheckSample(structureId, x, z, firstOceanFloorOccupiedHeight, firstOceanFloorOccupiedHeight, true));
			}
		}

		flatnessCheckSamples.forEach((flatnessCheckSample) -> Structurify.getDebugRenderer().addStructureFlatnessCheckSample(ChunkPos.asLong(structureCenter), flatnessCheckSample));
		Structurify.getDebugRenderer().addStructureFlatnessCheckInfo(ChunkPos.asLong(structureCenter), new StructureFlatnessCheckOverview(structureId, structureStart.getBoundingBox(), structurePieces, structureArea, minHeight, maxHeight, flatnessCheckHeightThreshold, totalFlatnessChecks, nonSolidFlatnessChecks, nonSolidFlatnessChecksThreshold, true));

		return true;
	}

	private StructureFlatnessCheck() {}
}