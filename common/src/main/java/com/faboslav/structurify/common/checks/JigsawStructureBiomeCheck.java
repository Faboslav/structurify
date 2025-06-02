package com.faboslav.structurify.common.checks;

import com.faboslav.structurify.common.config.data.StructureData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class JigsawStructureBiomeCheck
{
	public static boolean checkBiomes(
		StructureData structureData,
		HeightProvider startHeight,
		Structure.GenerationContext generationContext
	) {
		var biomeCheckDistance = (int) Math.ceil(structureData.getBiomeCheckDistance() / 16.0);

		if (biomeCheckDistance == 0 || generationContext.biomeSource() instanceof CheckerboardColumnBiomeSource) {
			return true;
		}

		ChunkPos chunkPos = generationContext.chunkPos();
		int y = startHeight.sample(generationContext.random(), new WorldGenerationContext(generationContext.chunkGenerator(), generationContext.heightAccessor()));
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
}
