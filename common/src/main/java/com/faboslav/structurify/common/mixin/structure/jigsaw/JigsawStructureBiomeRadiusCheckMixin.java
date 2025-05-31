package com.faboslav.structurify.common.mixin.structure.jigsaw;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.mixin.StructureMixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureBiomeRadiusCheckMixin extends StructureMixin
{
	@Shadow
	@Final
	private HeightProvider startHeight;

	@WrapMethod(
		method = "findGenerationPoint"
	)
	private Optional<GenerationStub> structurify$getStructurePosition(
		GenerationContext generationContext,
		Operation<Optional<GenerationStub>> original
	) {
		ResourceLocation structureId = structurify$getStructureIdentifier();

		if (
			structureId == null
			|| !Structurify.getConfig().getStructureData().containsKey(structureId.toString())
		) {
			return original.call(generationContext);
		}

		var structureData = Structurify.getConfig().getStructureData().get(structureId.toString());

		if (structureData.isFlatnessCheckEnabled()) {
			var biomeCheckResult = this.structurify$performFlatnessCheck(structureData, generationContext);

			if(!biomeCheckResult) {
				return Optional.empty();
			}
		}

		if (structureData.isBiomeCheckEnabled()) {
			var biomeCheckResult = this.structurify$performBiomeCheck(structureData, generationContext);

			if(!biomeCheckResult) {
				return Optional.empty();
			}
		}

		return original.call(generationContext);
	}

	@Unique
	private boolean structurify$performBiomeCheck(StructureData structureData, GenerationContext generationContext) {
		var biomeCheckDistance = (int) Math.ceil(structureData.getBiomeCheckDistance() / 16.0);

		if(biomeCheckDistance == 0 || generationContext.biomeSource() instanceof CheckerboardColumnBiomeSource) {
			return true;
		}

		ChunkPos chunkPos = generationContext.chunkPos();
		int y = this.startHeight.sample(generationContext.random(), new WorldGenerationContext(generationContext.chunkGenerator(), generationContext.heightAccessor()));
		var blockPos = new BlockPos(generationContext.chunkPos().getMinBlockX(), y, generationContext.chunkPos().getMinBlockZ());

		int sectionY = QuartPos.fromBlock(blockPos.getY());

		for (int curChunkX = chunkPos.x - biomeCheckDistance; curChunkX <= chunkPos.x + biomeCheckDistance; curChunkX++) {
			for (int curChunkZ = chunkPos.z - biomeCheckDistance; curChunkZ <= chunkPos.z + biomeCheckDistance; curChunkZ++) {
				Holder<Biome> biome = generationContext.biomeSource().getNoiseBiome(QuartPos.fromSection(curChunkX), sectionY, QuartPos.fromSection(curChunkZ), generationContext.randomState().sampler());
				if (!generationContext.validBiome().test(biome)) {
					return false;
				}
			}
		}

		return true;
	}

	@Unique
	private boolean structurify$performFlatnessCheck(StructureData structureData, GenerationContext generationContext) {
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
		int y = this.startHeight.sample(generationContext.random(), new WorldGenerationContext(generationContext.chunkGenerator(), generationContext.heightAccessor()));
		var blockPos = new BlockPos(chunkPos.getMinBlockX(), y, chunkPos.getMinBlockZ());

		// Structurify.getLogger().info("Flatness check starting at height: " + y);

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
