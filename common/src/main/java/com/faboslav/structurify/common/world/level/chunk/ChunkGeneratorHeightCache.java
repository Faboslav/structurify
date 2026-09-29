package com.faboslav.structurify.common.world.level.chunk;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ChunkGeneratorHeightCache
{
	private static final int MAX_CACHE_SIZE = 16_384;

	private static final ThreadLocal<ReferenceQueue<LevelHeightAccessor>> HEIGHT_ACCESSOR_QUEUE = ThreadLocal.withInitial(ReferenceQueue::new);

	private static final ThreadLocal<Map<ChunkGeneratorHeightCacheKey, Integer>> HEIGHT_CACHE = ThreadLocal.withInitial(() ->
		new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75F, true)
		{
			@Override
			protected boolean removeEldestEntry(Map.Entry<ChunkGeneratorHeightCacheKey, Integer> eldest) {
				return this.size() > MAX_CACHE_SIZE;
			}
		}
	);

	private ChunkGeneratorHeightCache() {
	}

	public static Integer getFirstFreeHeight(
		ChunkGenerator chunkGenerator,
		int x,
		int z,
		Heightmap.Types heightmapType,
		LevelHeightAccessor heightAccessor,
		RandomState randomState
	) {
		return get(chunkGenerator, x, z, heightmapType, heightAccessor, randomState);
	}

	public static void putFirstFreeHeight(
		ChunkGenerator chunkGenerator,
		int x,
		int z,
		Heightmap.Types heightmapType,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		int height
	) {
		put(chunkGenerator, x, z, heightmapType, heightAccessor, randomState, height);
	}

	public static Integer getFirstOccupiedHeight(
		ChunkGenerator chunkGenerator,
		int x,
		int z,
		Heightmap.Types heightmapType,
		LevelHeightAccessor heightAccessor,
		RandomState randomState
	) {
		Integer baseHeight = get(chunkGenerator, x, z, heightmapType, heightAccessor, randomState);

		return baseHeight == null ? null:baseHeight - 1;
	}

	public static void putFirstOccupiedHeight(
		ChunkGenerator chunkGenerator,
		int x,
		int z,
		Heightmap.Types heightmapType,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		int height
	) {
		put(chunkGenerator, x, z, heightmapType, heightAccessor, randomState, height + 1);
	}

	private static Integer get(
		ChunkGenerator chunkGenerator,
		int x,
		int z,
		Heightmap.Types heightmapType,
		LevelHeightAccessor heightAccessor,
		RandomState randomState
	) {
		try {
			Map<ChunkGeneratorHeightCacheKey, Integer> heightCache = HEIGHT_CACHE.get();
			removeCollectedEntries(heightCache);

			return heightCache.get(new ChunkGeneratorHeightCacheKey(chunkGenerator, x, z, heightmapType, heightAccessor, randomState));
		} finally {
			// Keeps the height accessor reachable while its key is being looked up
			Reference.reachabilityFence(heightAccessor);
		}
	}

	private static void put(
		ChunkGenerator chunkGenerator,
		int x,
		int z,
		Heightmap.Types heightmapType,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		int height
	) {
		try {
			Map<ChunkGeneratorHeightCacheKey, Integer> heightCache = HEIGHT_CACHE.get();
			removeCollectedEntries(heightCache);
			heightCache.put(new ChunkGeneratorHeightCacheKey(chunkGenerator, x, z, heightmapType, heightAccessor, randomState, HEIGHT_ACCESSOR_QUEUE.get()), height);
		} finally {
			// Keeps the height accessor reachable while its key is being stored
			Reference.reachabilityFence(heightAccessor);
		}
	}

	private static void removeCollectedEntries(Map<ChunkGeneratorHeightCacheKey, Integer> heightCache) {
		ReferenceQueue<LevelHeightAccessor> heightAccessorQueue = HEIGHT_ACCESSOR_QUEUE.get();
		Reference<? extends LevelHeightAccessor> collectedKey;

		while ((collectedKey = heightAccessorQueue.poll()) != null) {
			heightCache.remove(collectedKey);
		}
	}
}
