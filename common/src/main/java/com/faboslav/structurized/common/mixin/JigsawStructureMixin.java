package com.faboslav.structurized.common.mixin;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMixin extends Structure
{
	@Shadow
	@Final
	private int maxDistanceFromCenter;

	protected JigsawStructureMixin(Config config) {
		super(config);
	}

	@Inject(
		method = "getStructurePosition",
		at = @At(value = "TAIL"),
		cancellable = true
	)
	private void villagesAndPillages$getStructurePosition(
		Context context,
		CallbackInfoReturnable<Optional<StructurePosition>> cir
	) {
		int checkRadius = this.maxDistanceFromCenter;
		int stepSize = 16;
		BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 63, context.chunkPos().getStartZ());
		Predicate<RegistryEntry<Biome>> blackListedBiomes = biomeEntry -> {
			return biomeEntry.matchesKey(BiomeKeys.RIVER) || biomeEntry.matchesKey(BiomeKeys.FROZEN_RIVER);
		};

		for (int xOffset = -checkRadius; xOffset <= checkRadius; xOffset += stepSize) {
			for (int zOffset = -checkRadius; zOffset <= checkRadius; zOffset += stepSize) {
				int x = xOffset + blockPos.getX();
				int y = blockPos.getY();
				int z = zOffset + blockPos.getZ();

				if (xOffset % checkRadius == 0 && zOffset % checkRadius == 0) {
					var structurePosition = new StructurePosition(new BlockPos(x, y, z), collector -> {
					});
					var isBlacklistedBiome = this.isBiomeValid(structurePosition, context.chunkGenerator(), context.noiseConfig(), blackListedBiomes);
					if (isBlacklistedBiome) {
						// Structurized.getLogger().info("Prevented on x: " + structurePosition);
						cir.setReturnValue(Optional.empty());
						return;
					}
				}
			}
		}
	}

	private boolean isBiomeValid(
		StructurePosition result,
		ChunkGenerator chunkGenerator,
		NoiseConfig noiseConfig,
		Predicate<RegistryEntry<Biome>> validBiomes
	) {
		BlockPos blockPos = result.position();
		return validBiomes.test(chunkGenerator.getBiomeSource().getBiome(BiomeCoords.fromBlock(blockPos.getX()), BiomeCoords.fromBlock(blockPos.getY()), BiomeCoords.fromBlock(blockPos.getZ()), noiseConfig.getMultiNoiseSampler()));
	}
}
