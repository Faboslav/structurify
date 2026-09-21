package com.faboslav.structurify.common.world.level.structure;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.api.StructurifyStructurePlacement;
import com.faboslav.structurify.common.util.ChunkPosUtil;
import com.faboslav.structurify.common.util.RandomSpreadUtil;
import com.faboslav.structurify.common.world.level.structure.checks.StructureChecker;
import com.faboslav.structurify.common.world.level.structure.checks.StructureDistanceFromWorldCenterCheck;
import com.faboslav.structurify.common.world.level.structure.checks.debug.StructurePlacementAttempt;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public final class StructurePlacementResolver
{
	private static final ThreadLocal<Boolean> USE_ORIGINAL_PLACEMENT_CHUNKS_ONLY = new ThreadLocal<>();

	private static final int PLACEMENT_ATTEMPT_CHUNK_OFFSET_DEFAULT_VALUE = 4;

	public static boolean shouldUseOriginalPlacementChunksOnly() {
		return Boolean.TRUE.equals(USE_ORIGINAL_PLACEMENT_CHUNKS_ONLY.get());
	}

	public static void setUseOriginalPlacementChunksOnly(boolean useOriginalPlacementChunksOnly) {
		if (useOriginalPlacementChunksOnly) {
			USE_ORIGINAL_PLACEMENT_CHUNKS_ONLY.set(Boolean.TRUE);
		} else {
			USE_ORIGINAL_PLACEMENT_CHUNKS_ONLY.remove();
		}
	}

	public static ChunkPos resolveStructureChunk(
		String structureSetId,
		StructureSet structureSet,
		RandomSpreadStructurePlacement randomSpreadStructurePlacement,
		ChunkPos chunkPos,
		StructurePlacementContext context
	) {
		var structureChunkResolution = getStructureChunkResolution(structureSetId, structureSet, randomSpreadStructurePlacement, chunkPos, context);

		return ChunkPosUtil.createChunkPos(structureChunkResolution.structureChunk());
	}

	public static StructureChunkResolution getStructureChunkResolution(
		String structureSetId,
		StructureSet structureSet,
		RandomSpreadStructurePlacement randomSpreadStructurePlacement,
		ChunkPos chunkPos,
		StructurePlacementContext context
	) {
		int chunkX = ChunkPosUtil.getX(chunkPos);
		int chunkZ = ChunkPosUtil.getZ(chunkPos);
		ChunkPos originalStructureChunk = randomSpreadStructurePlacement.getPotentialStructureChunk(context.levelSeed(), chunkX, chunkZ);
		long originalStructureChunkKey = ChunkPosUtil.getChunkPosAsLong(originalStructureChunk);
		int placementAttempts = RandomSpreadUtil.getPlacementAttempts(structureSetId);

		if (placementAttempts <= 1) {
			return new StructureChunkResolution(originalStructureChunkKey, originalStructureChunkKey, 0, placementAttempts);
		}

		StructurifyChunkGenerator structurifyChunkGenerator = (StructurifyChunkGenerator) context.chunkGenerator();
		long structureChunkResolutionId = generateStructureChunkResolutionId(structureSetId, originalStructureChunk);

		return structurifyChunkGenerator.structurify$getResolvedStructureChunks().computeIfAbsent(
			structureChunkResolutionId,
			id -> resolveStructureChunkAttempts(structureSetId, structureSet, randomSpreadStructurePlacement, originalStructureChunk, chunkX, chunkZ, placementAttempts, context)
		);
	}

	private static StructureChunkResolution resolveStructureChunkAttempts(
		String structureSetId,
		StructureSet structureSet,
		RandomSpreadStructurePlacement randomSpreadStructurePlacement,
		ChunkPos originalStructureChunk,
		int chunkX,
		int chunkZ,
		int placementAttempts,
		StructurePlacementContext context
	) {
		long originalStructureChunkKey = ChunkPosUtil.getChunkPosAsLong(originalStructureChunk);
		var checkedAttemptChunks = new LongOpenHashSet();

		@Nullable BoundingBox largestStructureBoundingBox = null;
		int placementAttemptChunkOffset = PLACEMENT_ATTEMPT_CHUNK_OFFSET_DEFAULT_VALUE;

		for (int attempt = 0; attempt < placementAttempts; attempt++) {
			if (attempt == 1) {
				placementAttemptChunkOffset = getPlacementAttemptChunkOffset(structureSet, largestStructureBoundingBox);
			}

			ChunkPos attemptChunkPos = attempt == 0
				? originalStructureChunk
				: RandomSpreadUtil.getPlacementAttemptChunk(randomSpreadStructurePlacement, context.levelSeed(), chunkX, chunkZ, attempt, placementAttemptChunkOffset);

			if (!checkedAttemptChunks.add(ChunkPosUtil.getChunkPosAsLong(attemptChunkPos))) {
				continue;
			}

			if (attempt > 0 && !canStructureSetGenerateInBiome(structureSet, attemptChunkPos, context)) {
				continue;
			}

			var structurePlacementAttempt = new StructurePlacementAttempt(attempt, placementAttempts, originalStructureChunk);
			var structurePlacementAttemptResult = tryGenerateStructure(structureSet, attemptChunkPos, structurePlacementAttempt, context);

			if (structurePlacementAttemptResult.canGenerate()) {
				return new StructureChunkResolution(ChunkPosUtil.getChunkPosAsLong(attemptChunkPos), originalStructureChunkKey, attempt, placementAttempts);
			}

			BoundingBox attemptStructureBoundingBox = structurePlacementAttemptResult.structureBoundingBox();

			if (attemptStructureBoundingBox != null && (largestStructureBoundingBox == null || getHorizontalSpan(attemptStructureBoundingBox) > getHorizontalSpan(largestStructureBoundingBox))) {
				largestStructureBoundingBox = attemptStructureBoundingBox;
			}
		}

		return new StructureChunkResolution(originalStructureChunkKey, originalStructureChunkKey, 0, placementAttempts);
	}

	private static int getPlacementAttemptChunkOffset(
		StructureSet structureSet,
		@Nullable BoundingBox largestStructureBoundingBox
	) {
		if (largestStructureBoundingBox != null) {
			return Math.max(1, ChunkPosUtil.getChunkSpan(getHorizontalSpan(largestStructureBoundingBox)));
		}

		return RandomSpreadUtil.getPlacementAttemptChunkOffset(structureSet, PLACEMENT_ATTEMPT_CHUNK_OFFSET_DEFAULT_VALUE);
	}

	private static int getHorizontalSpan(BoundingBox structureBoundingBox) {
		return Math.max(structureBoundingBox.getXSpan(), structureBoundingBox.getZSpan());
	}

	private static StructurePlacementAttemptResult tryGenerateStructure(
		StructureSet structureSet,
		ChunkPos attemptChunkPos,
		StructurePlacementAttempt structurePlacementAttempt,
		StructurePlacementContext context
	) {
		var remainingStructureSelectionEntries = new ArrayList<>(structureSet.structures());

		WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(0L));
		worldgenRandom.setLargeFeatureSeed(context.levelSeed(), ChunkPosUtil.getX(attemptChunkPos), ChunkPosUtil.getZ(attemptChunkPos));

		@Nullable BoundingBox largestStructureBoundingBox = null;
		int totalWeight = 0;

		for (var structureSelectionEntry : remainingStructureSelectionEntries) {
			totalWeight += structureSelectionEntry.weight();
		}

		while (!remainingStructureSelectionEntries.isEmpty() && totalWeight > 0) {
			int selectedWeight = worldgenRandom.nextInt(totalWeight);
			int selectedEntryIndex = 0;

			for (var structureSelectionEntry : remainingStructureSelectionEntries) {
				selectedWeight -= structureSelectionEntry.weight();

				if (selectedWeight < 0) {
					break;
				}

				selectedEntryIndex++;
			}

			var selectedStructureSelectionEntry = remainingStructureSelectionEntries.get(selectedEntryIndex);
			StructureStart structureStart = generateStructure(selectedStructureSelectionEntry, attemptChunkPos, context);

			if (structureStart.isValid()) {
				var structure = (StructurifyStructure) selectedStructureSelectionEntry.structure().value();
				BoundingBox structureBoundingBox = structureStart.getBoundingBox();

				if (largestStructureBoundingBox == null || getHorizontalSpan(structureBoundingBox) > getHorizontalSpan(largestStructureBoundingBox)) {
					largestStructureBoundingBox = structureBoundingBox;
				}

				var structureCheckResult = StructureChecker.testStructure(
					structureStart,
					structure,
					context.chunkGenerator(),
					context.heightAccessor(),
					context.randomState(),
					context.biomeSource(),
					structurePlacementAttempt
				);

				if (structureCheckResult) {
					return new StructurePlacementAttemptResult(true, largestStructureBoundingBox);
				}
			}

			remainingStructureSelectionEntries.remove(selectedEntryIndex);
			totalWeight -= selectedStructureSelectionEntry.weight();
		}

		return new StructurePlacementAttemptResult(false, largestStructureBoundingBox);
	}

	private static StructureStart generateStructure(
		StructureSet.StructureSelectionEntry structureSelectionEntry,
		ChunkPos attemptChunkPos,
		StructurePlacementContext context
	) {
		var structureKey = structureSelectionEntry.structure().unwrapKey();

		if (structureKey.isPresent()) {
			Identifier structureName = structureKey.get()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/;
			var structureData = Structurify.getConfig().getStructureData().getOrDefault(structureName.toString(), null);

			if (structureData != null) {
				if (structureData.isDisabled()) {
					return StructureStart.INVALID_START;
				}

				var distanceFromWorldCenterCheckData = StructureDistanceFromWorldCenterCheck.getDistanceFromWorldCenterData(structureName, structureData);

				if (distanceFromWorldCenterCheckData != null && !StructureDistanceFromWorldCenterCheck.checkDistanceFromWorldCenter(distanceFromWorldCenterCheckData, attemptChunkPos)) {
					return StructureStart.INVALID_START;
				}
			}
		}

		try {
			var structure = structureSelectionEntry.structure().value();
			var structureBiomes = structure.biomes();
			var validBiome = new SpeculativeStructureBiomePredicate(structureBiomes::contains);

			//? if >= 26.3 {
			return structure.generate(structureSelectionEntry.structure(), context.level(), context.registryAccess(), context.chunkGenerator(), context.biomeSource(), context.climateSampler(), context.randomState(), context.structureTemplateManager(), context.levelSeed(), attemptChunkPos, 0, context.heightAccessor(), validBiome);
			//?} else if >= 1.21.4 {
			//return structure.generate(structureSelectionEntry.structure(), context.level(), context.registryAccess(), context.chunkGenerator(), context.biomeSource(), context.randomState(), context.structureTemplateManager(), context.levelSeed(), attemptChunkPos, 0, context.heightAccessor(), validBiome);
			//?} else {
			//return structure.generate(context.registryAccess(), context.chunkGenerator(), context.biomeSource(), context.randomState(), context.structureTemplateManager(), context.levelSeed(), attemptChunkPos, 0, context.heightAccessor(), validBiome);
			//?}
		} catch (Throwable e) {
			return StructureStart.INVALID_START;
		}
	}

	private static boolean canStructureSetGenerateInBiome(
		StructureSet structureSet,
		ChunkPos attemptChunkPos,
		StructurePlacementContext context
	) {
		try {
			//? if >= 26.3 {
			Holder<Biome> biome = context.biomeSource().createResolver(context.climateSampler()).getNoiseBiome(
				QuartPos.fromBlock(attemptChunkPos.getMiddleBlockX()),
				QuartPos.fromBlock(getSurfaceY(attemptChunkPos, context)),
				QuartPos.fromBlock(attemptChunkPos.getMiddleBlockZ())
			);
			//?} else {
			/*Holder<Biome> biome = context.biomeSource().getNoiseBiome(
				QuartPos.fromBlock(attemptChunkPos.getMiddleBlockX()),
				QuartPos.fromBlock(getSurfaceY(attemptChunkPos, context)),
				QuartPos.fromBlock(attemptChunkPos.getMiddleBlockZ()),
				context.randomState().sampler()
			);
			*///?}

			for (var structureSelectionEntry : structureSet.structures()) {
				if (structureSelectionEntry.structure().value().biomes().contains(biome)) {
					return true;
				}
			}

			return false;
		} catch (Throwable e) {
			return true;
		}
	}

	private static int getSurfaceY(ChunkPos chunkPos, StructurePlacementContext context) {
		return context.chunkGenerator().getFirstFreeHeight(
			chunkPos.getMiddleBlockX(),
			chunkPos.getMiddleBlockZ(),
			Heightmap.Types.WORLD_SURFACE_WG,
			context.heightAccessor(),
			context.randomState()
		);
	}

	public static StructurePlacementAttempt getStructurePlacementAttempt(
		StructureStart structureStart,
		ServerLevel serverLevel
	) {
		if (structureStart == null || structureStart == StructureStart.INVALID_START || !structureStart.isValid()) {
			return StructurePlacementAttempt.of(ChunkPos.ZERO);
		}

		var generatorState = serverLevel.getChunkSource().getGeneratorState();

		for (var structurePlacement : generatorState.getPlacementsForStructure(Holder.direct(structureStart.getStructure()))) {
			if (!(structurePlacement instanceof RandomSpreadStructurePlacement randomSpreadStructurePlacement)) {
				continue;
			}

			var structurifyStructurePlacement = (StructurifyStructurePlacement) structurePlacement;
			var structureSetId = structurifyStructurePlacement.structurify$getStructureSetId();
			var structureSet = structurifyStructurePlacement.structurify$getStructureSet();

			if (structureSetId == null || structureSet == null || RandomSpreadUtil.getPlacementAttempts(structureSetId) <= 1) {
				continue;
			}

			ChunkPos structureChunkPos = structureStart.getChunkPos();
			var structureChunkResolution = getStructureChunkResolution(structureSetId, structureSet, randomSpreadStructurePlacement, structureChunkPos, StructurePlacementContext.of(serverLevel));

			if (structureChunkResolution.structureChunk() == ChunkPosUtil.getChunkPosAsLong(structureChunkPos)) {
				return new StructurePlacementAttempt(
					structureChunkResolution.attempt(),
					structureChunkResolution.placementAttempts(),
					ChunkPosUtil.createChunkPos(structureChunkResolution.originalStructureChunk())
				);
			}
		}

		return StructurePlacementAttempt.of(structureStart.getChunkPos());
	}

	public static long generateStructureChunkResolutionId(String structureSetId, ChunkPos chunkPos) {
		long structureSetHash = structureSetId.hashCode() & 0xffffffffL;
		long chunkHash = ChunkPosUtil.getChunkPosAsLong(chunkPos);

		long structureChunkResolutionId = structureSetHash * 0x9E3779B97F4A7C15L + chunkHash;
		structureChunkResolutionId = (structureChunkResolutionId ^ (structureChunkResolutionId >>> 30)) * 0xBF58476D1CE4E5B9L;
		structureChunkResolutionId = (structureChunkResolutionId ^ (structureChunkResolutionId >>> 27)) * 0x94D049BB133111EBL;

		return structureChunkResolutionId ^ (structureChunkResolutionId >>> 31);
	}

	private record StructurePlacementAttemptResult(boolean canGenerate, @Nullable BoundingBox structureBoundingBox) {}
}
