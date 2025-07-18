package com.faboslav.structurify.common.checks;

import com.faboslav.structurify.common.config.data.StructureData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class JigsawStructureFlatnessCheck
{
	public static boolean checkFlatness(
		StructureData structureData,
		HeightProvider startHeight,
		Structure.GenerationContext generationContext
	) {
		int flatnessCheckDistance = structureData.getFlatnessCheckDistance();
		int flatnessCheckThreshold = structureData.getFlatnessCheckThreshold();
		boolean areAirBlocksAllowed = structureData.areAirBlocksAllowedInFlatnessCheck();
		boolean areLiquidBlocksAllowed = structureData.areLiquidBlocksAllowedInFlatnessCheck();

		if (flatnessCheckDistance == 0 || flatnessCheckThreshold == 0) {
			return true;
		}

		int offsetStep = (int) Math.ceil(flatnessCheckDistance / 2.0F);
		int stepsPerAxis = (2 * flatnessCheckDistance) / offsetStep + 1;
		int stepAmount = stepsPerAxis * stepsPerAxis;
		int allowedAirBlockSteps = stepAmount / 2;
		int allowedLiquidBlockSteps = stepAmount / 2;

		var chunkGenerator = generationContext.chunkGenerator();
		var heightAccessor = generationContext.heightAccessor();
		var randomState = generationContext.randomState();
		var chunkPos = generationContext.chunkPos();

		int y = startHeight.sample(generationContext.random(), new WorldGenerationContext(chunkGenerator, heightAccessor));
		var basePos = new BlockPos(chunkPos.getMinBlockX(), y, chunkPos.getMinBlockZ());
		int baseX = basePos.getX();
		int baseZ = basePos.getZ();

		int minHeight = Integer.MAX_VALUE;
		int maxHeight = Integer.MIN_VALUE;
		int airBlockSteps = 0;
		int fluidBlockSteps = 0;

		for (int xOffset = -flatnessCheckDistance; xOffset <= flatnessCheckDistance; xOffset += offsetStep) {
			for (int zOffset = -flatnessCheckDistance; zOffset <= flatnessCheckDistance; zOffset += offsetStep) {
				int x = baseX + xOffset;
				int z = baseZ + zOffset;

				int height = chunkGenerator.getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);

				if (height > maxHeight) {
					maxHeight = height;
					if (maxHeight - minHeight > flatnessCheckThreshold) {
						return false;
					}
				}
				if (height < minHeight) {
					minHeight = height;
					if (maxHeight - minHeight > flatnessCheckThreshold) {
						return false;
					}
				}

				if (!areAirBlocksAllowed || !areLiquidBlocksAllowed) {
					BlockState blockState = null;

					if (!areAirBlocksAllowed) {
						if (blockState == null) {
							blockState = chunkGenerator.getBaseColumn(x, z, heightAccessor, randomState).getBlock(height);
						}
						if (blockState.isAir()) {
							airBlockSteps++;
							if (airBlockSteps >= allowedAirBlockSteps) {
								return false;
							}
						}
					}

					if (!areLiquidBlocksAllowed) {
						if (blockState == null) {
							blockState = chunkGenerator.getBaseColumn(x, z, heightAccessor, randomState).getBlock(height);
						}
						if (!blockState.getFluidState().isEmpty()) {
							fluidBlockSteps++;
							if (fluidBlockSteps >= allowedLiquidBlockSteps) {
								return false;
							}
						}
					}
				}
			}
		}

		return true;
	}
}