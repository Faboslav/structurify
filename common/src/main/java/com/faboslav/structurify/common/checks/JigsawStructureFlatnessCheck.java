package com.faboslav.structurify.common.checks;

import com.faboslav.structurify.common.config.data.StructureData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class JigsawStructureFlatnessCheck
{
	public static boolean checkFlatness(StructureData structureData, HeightProvider startHeight, Structure.GenerationContext generationContext) {
		var flatnessCheckDistance = structureData.getFlatnessCheckDistance();
		var offsetStep = (int) Math.ceil(flatnessCheckDistance / 2.0F);
		int stepAmount = (int) Math.pow((double) (2 * flatnessCheckDistance) / offsetStep + 1, 2);
		int allowedAirBlockSteps = stepAmount / 2;
		int allowedLiquidBlockSteps = stepAmount / 2;
		var flatnessCheckThreshold = structureData.getFlatnessCheckThreshold();
		var areAirBlocksAllowed = structureData.areAirBlocksAllowedInFlatnessCheck();
		var areLiquidBlocksAllowed = structureData.areLiquidBlocksAllowedInFlatnessCheck();

		if(flatnessCheckDistance == 0 || flatnessCheckThreshold == 0) {
			return true;
		}

		ChunkPos chunkPos = generationContext.chunkPos();
		int y = startHeight.sample(generationContext.random(), new WorldGenerationContext(generationContext.chunkGenerator(), generationContext.heightAccessor()));
		var blockPos = new BlockPos(chunkPos.getMinBlockX(), y, chunkPos.getMinBlockZ());

		int baseX = blockPos.getX();
		int baseZ = blockPos.getZ();
		int minHeight = Integer.MAX_VALUE;
		int maxHeight = Integer.MIN_VALUE;

		int airBlockSteps = 0;
		int fluidBlockSteps = 0;

		for (int xOffset = -flatnessCheckDistance; xOffset <= flatnessCheckDistance; xOffset += offsetStep) {
			for (int zOffset = -flatnessCheckDistance; zOffset <= flatnessCheckDistance; zOffset += offsetStep) {
				int x = xOffset + baseX;
				int z = zOffset + baseZ;
				int height = generationContext.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, generationContext.heightAccessor(), generationContext.randomState());

				minHeight = Math.min(minHeight, height);
				maxHeight = Math.max(maxHeight, height);

				if (maxHeight - minHeight > flatnessCheckThreshold) {
					return false;
				}

				if(!areAirBlocksAllowed || !areLiquidBlocksAllowed) {
					NoiseColumn blockView = generationContext.chunkGenerator().getBaseColumn(
						x,
						z,
						generationContext.heightAccessor(),
						generationContext.randomState()
					);

					BlockState blockState = blockView.getBlock(height);

					// Structurify.getLogger().info("blockstate: " + blockState + " at: " + x + ", " + height + ", " + z);

					if(!areAirBlocksAllowed && blockState.isAir()) {
						airBlockSteps++;

						if(airBlockSteps >= allowedAirBlockSteps) {
							// Structurify.getLogger().info("air");
							return false;
						}
					}

					if(!areLiquidBlocksAllowed && !blockState.getFluidState().isEmpty()) {
						fluidBlockSteps++;

						if(fluidBlockSteps >= allowedLiquidBlockSteps) {
							// Structurify.getLogger().info("liquid");
							return false;
						}
					}
				}
			}
		}

		return true;
	}
}
