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

	public static ChunkPos createChunkPos(long chunkPos) {
		//? if >= 26.1 {
		return ChunkPos.unpack(chunkPos);
		//?} else {
		//return new ChunkPos(chunkPos);
		//?}
	}

	public static long getChunkPosAsLong(ChunkPos chunkPos) {

		//? if >= 26.1 {
		return chunkPos.pack();
		//?} else {
		//return chunkPos.toLong();
		//?}
	}

	public static int getChunkSpan(int blocks) {
		return (blocks + SectionPos.SECTION_SIZE - 1) / SectionPos.SECTION_SIZE;
	}

	public static int getX(ChunkPos chunkPos) {
		//? if >= 26.1 {
		return chunkPos.x();
		//?} else {
		//return chunkPos.x;
		//?}
	}

	public static int getZ(ChunkPos chunkPos) {
		//? if >= 26.1 {
		return chunkPos.z();
		//?} else {
		//return chunkPos.z;
		//?}
	}
}
