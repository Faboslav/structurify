package com.faboslav.structurify.common.world.level.structure;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

//? if >= 1.21.4 {
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
//?}

//? if >= 26.3 {
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
//?}

public record StructurePlacementContext(
	ChunkGenerator chunkGenerator,
	RegistryAccess registryAccess,
	BiomeSource biomeSource,
	RandomState randomState,
	StructureTemplateManager structureTemplateManager,
	long levelSeed,
	LevelHeightAccessor heightAccessor/*? if >= 1.21.4 {*/,
	ResourceKey<Level> level/*?}*//*? if >= 26.3 {*/,
	Climate.Sampler climateSampler/*?}*/
)
{
	public static StructurePlacementContext of(ServerLevel serverLevel) {
		var chunkSource = serverLevel.getChunkSource();
		var chunkGenerator = chunkSource.getGenerator();

		return new StructurePlacementContext(
			chunkGenerator,
			serverLevel.registryAccess(),
			chunkGenerator.getBiomeSource(),
			chunkSource.randomState(),
			serverLevel.getStructureTemplateManager(),
			chunkSource.getGeneratorState().getLevelSeed(),
			serverLevel/*? if >= 1.21.4 {*/,
			serverLevel.dimension()/*?}*//*? if >= 26.3 {*/,
			chunkSource.randomState().createClimateSampler(SamplerContext.builder().enableCaches().build())/*?}*/
		);
	}
}
