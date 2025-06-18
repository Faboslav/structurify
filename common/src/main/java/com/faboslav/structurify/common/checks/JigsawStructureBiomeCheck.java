package com.faboslav.structurify.common.checks;

import com.faboslav.structurify.common.config.data.StructureData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class JigsawStructureBiomeCheck
{
	public static boolean checkBiomes(
		StructureData structureData,
		HeightProvider startHeight,
		Structure.GenerationContext generationContext,
		HolderSet<Biome> blacklistedBiomes
	) {
		var biomeCheckDistance = (int) Math.ceil(structureData.getBiomeCheckDistance() / 16.0);

		if (biomeCheckDistance == 0 || generationContext.biomeSource() instanceof CheckerboardColumnBiomeSource) {
			return true;
		}

		if(structureData.getBiomeCheckMode() == StructureData.BiomeCheckMode.BLACKLIST && structureData.getBiomeCheckBlacklistedBiomes().isEmpty()) {
			return true;
		}

		ChunkPos chunkPos = generationContext.chunkPos();

		for (int curChunkX = chunkPos.x - biomeCheckDistance; curChunkX <= chunkPos.x + biomeCheckDistance; curChunkX++) {
			for (int curChunkZ = chunkPos.z - biomeCheckDistance; curChunkZ <= chunkPos.z + biomeCheckDistance; curChunkZ++) {

				int blockX = curChunkX << 4;
				int blockZ = curChunkZ << 4;

				int surfaceY = generationContext.chunkGenerator().getFirstFreeHeight(
					blockX,
					blockZ,
					Heightmap.Types.WORLD_SURFACE_WG,
					generationContext.heightAccessor(),
					generationContext.randomState()
				);

				Holder<Biome> biome = generationContext.biomeSource().getNoiseBiome(
					QuartPos.fromBlock(blockX),
					QuartPos.fromBlock(surfaceY),
					QuartPos.fromBlock(blockZ),
					generationContext.randomState().sampler()
				);

				if (structureData.getBiomeCheckMode() == StructureData.BiomeCheckMode.STRICT && !generationContext.validBiome().test(biome)) {
					return false;
				}

				if (structureData.getBiomeCheckMode() == StructureData.BiomeCheckMode.BLACKLIST && blacklistedBiomes.contains(biome)) {
					return false;
				}

				// TODO this check will not work for nether because of the Y level checks
				//BlockPos blockPos = new BlockPos(blockX, surfaceY, blockZ);
				//Structurify.getLogger().info("Structure biome " + biome + " at surface pos: " + blockPos);
			}
		}

		return true;
	}
}
