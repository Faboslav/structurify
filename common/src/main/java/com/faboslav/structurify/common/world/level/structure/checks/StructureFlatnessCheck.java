package com.faboslav.structurify.common.world.level.structure.checks;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.structure.FlatnessCheckData;
import com.faboslav.structurify.common.util.ChunkPosUtil;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureFlatnessCheckOverview;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructureFlatnessCheckSample;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public final class StructureFlatnessCheck
{
	@Nullable
	public static FlatnessCheckData getFlatnessCheckData(
		StructureCheckData structureCheckData
	) {
		var structure = structureCheckData.getStructure();
		var structureId = structureCheckData.getStructureId();
		var globalNamespaceData = structure.structurify$getGlobalStructureNamespaceData();
		var structureNamespaceData = structure.structurify$getStructureNamespaceData(structureId);
		var structureData = structure.structurify$getStructureData(structureId);

		@Nullable
		FlatnessCheckData flatnessCheckDataToCheck = null;

		if(globalNamespaceData != null) {
			flatnessCheckDataToCheck = globalNamespaceData.getFlatnessCheckData();;
		}

		if(structureNamespaceData != null) {
			var namespaceFlatnessCheckData = structureNamespaceData.getFlatnessCheckData();

			if (namespaceFlatnessCheckData.isOverridingGlobalFlatnessCheck() || namespaceFlatnessCheckData.isEnabled()) {
				flatnessCheckDataToCheck = namespaceFlatnessCheckData;
			}
		}

		if(structureData != null) {
			var structureFlatnessCheckData = structureData.getFlatnessCheckData();

			if (structureFlatnessCheckData.isOverridingGlobalFlatnessCheck() || structureFlatnessCheckData.isEnabled()) {
				flatnessCheckDataToCheck = structureFlatnessCheckData;
			}
		}

		return flatnessCheckDataToCheck;
	}

	public static boolean canDoFlatnessCheck(
		StructureCheckData structureCheckData,
		@Nullable FlatnessCheckData flatnessCheckData
	) {
		var structure = structureCheckData.getStructure();

		if (flatnessCheckData == null || !flatnessCheckData.isEnabled()) {
			return false;
		}

		var structureData = structure.structurify$getStructureData();

		if(structureData == null) {
			return false;
		}

		return true;
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
		var structureCenterChunkPos = ChunkPosUtil.getChunkPosAsLong(ChunkPosUtil.createChunkPos(structureCenter));
		var structureArea = structureCheckData.getStructureArea();

		Set<StructureFlatnessCheckSample> flatnessCheckSamples = new HashSet<>();

		int flatnessCheckHeightThreshold = Mth.clamp(
			(int) Math.round(Math.sqrt(structureArea) * 0.35),
			3,
			24
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

			if(currentFlatnessCheck == 0) {
				minHeight = firstOceanFloorOccupiedHeight;
				maxHeight = firstOceanFloorOccupiedHeight;
			}

			if (firstOceanFloorOccupiedHeight > maxHeight) {
				maxHeight = firstOceanFloorOccupiedHeight;
				if (maxHeight - minHeight > flatnessCheckHeightThreshold) {
					Structurify.getConfig().getDebugData().addStructureFlatnessCheckInfo(structureCenterChunkPos, new StructureFlatnessCheckOverview(structureId, structureStart.getBoundingBox(), structurePieces, structureArea, minHeight, maxHeight, flatnessCheckHeightThreshold, totalFlatnessChecks, nonSolidFlatnessChecks, nonSolidFlatnessChecksThreshold, false));
					return false;
				}
			}

			if (firstOceanFloorOccupiedHeight < minHeight) {
				minHeight = firstOceanFloorOccupiedHeight;
				if (maxHeight - minHeight > flatnessCheckHeightThreshold) {
					Structurify.getConfig().getDebugData().addStructureFlatnessCheckInfo(structureCenterChunkPos, new StructureFlatnessCheckOverview(structureId, structureStart.getBoundingBox(), structurePieces, structureArea, minHeight, maxHeight, flatnessCheckHeightThreshold, totalFlatnessChecks, nonSolidFlatnessChecks, nonSolidFlatnessChecksThreshold, false));
					return false;
				}
			}

			if (!flatnessCheckData.areNonSolidBlocksAllowed()) {
				int remainingFlatnessChecks = totalFlatnessChecks - currentFlatnessCheck;

				if ((nonSolidFlatnessChecks + remainingFlatnessChecks >= nonSolidFlatnessChecksThreshold) || Structurify.getConfig().getDebugData().isEnabled()) {
					int firstWorldSurfaceFreeHeight = chunkGenerator.getFirstFreeHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
					boolean isSolid = firstWorldSurfaceFreeHeight - firstOceanFloorOccupiedHeight == 1;

					if (Structurify.getConfig().getDebugData().isEnabled()) {
						flatnessCheckSamples.add(new StructureFlatnessCheckSample(structureId, x, z, firstOceanFloorOccupiedHeight, firstWorldSurfaceFreeHeight, isSolid));
					}

					if (!isSolid) {
						nonSolidFlatnessChecks++;

						if (nonSolidFlatnessChecks >= nonSolidFlatnessChecksThreshold) {
							Structurify.getConfig().getDebugData().addStructureFlatnessCheckInfo(structureCenterChunkPos, new StructureFlatnessCheckOverview(structureId, structureStart.getBoundingBox(), structurePieces, structureArea, minHeight, maxHeight, flatnessCheckHeightThreshold, totalFlatnessChecks, nonSolidFlatnessChecks, nonSolidFlatnessChecksThreshold, false));
							return false;
						}
					}
				}
			} else {
				flatnessCheckSamples.add(new StructureFlatnessCheckSample(structureId, x, z, firstOceanFloorOccupiedHeight, firstOceanFloorOccupiedHeight, true));
			}
		}

		flatnessCheckSamples.forEach((flatnessCheckSample) -> Structurify.getConfig().getDebugData().addStructureFlatnessCheckSample(structureCenterChunkPos, flatnessCheckSample));
		Structurify.getConfig().getDebugData().addStructureFlatnessCheckInfo(structureCenterChunkPos, new StructureFlatnessCheckOverview(structureId, structureStart.getBoundingBox(), structurePieces, structureArea, minHeight, maxHeight, flatnessCheckHeightThreshold, totalFlatnessChecks, nonSolidFlatnessChecks, nonSolidFlatnessChecksThreshold, true));

		return true;
	}

	private StructureFlatnessCheck() {
	}
}