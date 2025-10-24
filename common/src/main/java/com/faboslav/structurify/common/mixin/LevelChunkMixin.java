package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.world.level.structure.checks.StructureChecker;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

//? if <= 1.21.8 {
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
//?}

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess
{
	//? if >= 1.21.9 {
	/*public LevelChunkMixin(
		ChunkPos chunkPos,
		UpgradeData upgradeData,
		LevelHeightAccessor levelHeightAccessor,
		PalettedContainerFactory palettedContainerFactory,
		long l,
		@Nullable LevelChunkSection[] levelChunkSections,
		@Nullable BlendingData blendingData
	) {
		super(chunkPos, upgradeData, levelHeightAccessor, palettedContainerFactory, l, levelChunkSections, blendingData);
	}
	*///?} else {
	public LevelChunkMixin(
		ChunkPos chunkPos,
		UpgradeData upgradeData,
		LevelHeightAccessor heightAccessor,
		Registry<Biome> biomeRegistry,
		long inhabitedTime,
		@Nullable LevelChunkSection[] sections,
		@Nullable BlendingData blendingData
	) {
		super(chunkPos, upgradeData, heightAccessor, biomeRegistry, inhabitedTime, sections, blendingData);
	}
	//?}

	@Shadow
	public abstract Level getLevel();

	@WrapMethod(
		method = "runPostLoad"
	)
	private void structurify$runPostLoad(Operation<Void> original) {
		original.call();

		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		ServerLevel serverLevel = (ServerLevel) this.getLevel();
		ServerChunkCache chunkSource = serverLevel.getChunkSource();
		ChunkGenerator chunkGenerator = chunkSource.getGenerator();
		RandomState randomState = chunkSource.randomState();
		BiomeSource biomeSource = chunkGenerator.getBiomeSource();

		for (var structureStartEntry : this.getAllStarts().entrySet()) {
			StructureChecker.debugCheckStructure(structureStartEntry.getValue(), (StructurifyStructure) structureStartEntry.getKey(), chunkGenerator, this.levelHeightAccessor, randomState, biomeSource);
		}
	}
}
