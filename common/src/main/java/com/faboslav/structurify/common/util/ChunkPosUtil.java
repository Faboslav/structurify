package com.faboslav.structurify.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

public final class ChunkPosUtil
{
	public static ChunkPos createChunkPos(BlockPos blockPos) {
		return createChunkPos(blockPos.getX(), blockPos.getZ());
	}

	public static ChunkPos createChunkPos(int x, int z) {
		return new ChunkPos(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
	}

	public static long getChunkPosAsLong(ChunkPos chunkPos) {

		//? if >= 26.1 {
		return chunkPos.pack();
		//?} else {
		/*return chunkPos.toLong();
		*///?}
	}
}
