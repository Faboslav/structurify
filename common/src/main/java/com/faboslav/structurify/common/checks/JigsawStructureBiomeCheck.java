package com.faboslav.structurify.common.checks;

import com.faboslav.structurify.common.config.data.StructureData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
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
		int biomeCheckDistance = (int) Math.ceil(structureData.getBiomeCheckDistance() / 16.0);

		if (biomeCheckDistance == 0 || generationContext.biomeSource() instanceof CheckerboardColumnBiomeSource) {
			return true;
		}

		var biomeCheckMode = structureData.getBiomeCheckMode();
		if (biomeCheckMode == StructureData.BiomeCheckMode.BLACKLIST && structureData.getBiomeCheckBlacklistedBiomes().isEmpty()) {
			return true;
		}

		var chunkPos = generationContext.chunkPos();
		var biomeSource = generationContext.biomeSource();
		var randomState = generationContext.randomState();
		var biomeSampler = randomState.sampler();
		int blockX = (chunkPos.x << 4);
		int blockZ = (chunkPos.z << 4);

		int surfaceY = generationContext.chunkGenerator().getFirstFreeHeight(
			blockX,
			blockZ,
			Heightmap.Types.WORLD_SURFACE_WG,
			generationContext.heightAccessor(),
			generationContext.randomState()
		);

		for (int curChunkX = chunkPos.x - biomeCheckDistance; curChunkX <= chunkPos.x + biomeCheckDistance; curChunkX++) {
			for (int curChunkZ = chunkPos.z - biomeCheckDistance; curChunkZ <= chunkPos.z + biomeCheckDistance; curChunkZ++) {
				blockX = (curChunkX << 4) + 8;
				blockZ = (curChunkZ << 4) + 8;

				Holder<Biome> biome = biomeSource.getNoiseBiome(
					QuartPos.fromBlock(blockX),
					QuartPos.fromBlock(256),
					QuartPos.fromBlock(blockZ),
					biomeSampler
				);

				switch (biomeCheckMode) {
					case STRICT -> {
						if (!generationContext.validBiome().test(biome)) return false;
					}
					case BLACKLIST -> {
						if (blacklistedBiomes.contains(biome)) return false;
					}
				}
			}
		}

		return true;
	}
}
