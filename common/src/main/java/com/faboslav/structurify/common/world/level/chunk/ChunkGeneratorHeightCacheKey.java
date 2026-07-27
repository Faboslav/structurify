package com.faboslav.structurify.common.world.level.chunk;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

final class ChunkGeneratorHeightCacheKey
{
	private final ChunkGenerator chunkGenerator;
	private final int x;
	private final int z;
	private final Heightmap.Types heightmapType;
	private final LevelHeightAccessor heightAccessor;
	private final RandomState randomState;
	private final int hashCode;

	ChunkGeneratorHeightCacheKey(
		ChunkGenerator chunkGenerator,
		int x,
		int z,
		Heightmap.Types heightmapType,
		LevelHeightAccessor heightAccessor,
		RandomState randomState
	) {
		this.chunkGenerator = chunkGenerator;
		this.x = x;
		this.z = z;
		this.heightmapType = heightmapType;
		this.heightAccessor = heightAccessor;
		this.randomState = randomState;

		int hash = System.identityHashCode(chunkGenerator);
		hash = 31 * hash + x;
		hash = 31 * hash + z;
		hash = 31 * hash + heightmapType.hashCode();
		hash = 31 * hash + System.identityHashCode(heightAccessor);
		hash = 31 * hash + System.identityHashCode(randomState);

		this.hashCode = hash;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ChunkGeneratorHeightCacheKey other)) {
			return false;
		}

		return this.chunkGenerator == other.chunkGenerator
			   && this.x == other.x
			   && this.z == other.z
			   && this.heightmapType == other.heightmapType
			   && this.heightAccessor == other.heightAccessor
			   && this.randomState == other.randomState;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}
}
