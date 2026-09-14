package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.common.api.StructurifyStructurePlacement;
import com.faboslav.structurify.common.api.StructurifyStructureSelectionEntry;
import com.faboslav.structurify.common.util.RandomSpreadUtil;
import com.faboslav.structurify.common.world.level.chunk.ChunkGeneratorHeightCache;
import com.faboslav.structurify.common.world.level.structure.StructureChunkResolution;
import com.faboslav.structurify.common.world.level.structure.StructurePlacementContext;
import com.faboslav.structurify.common.world.level.structure.StructurePlacementResolver;
import com.faboslav.structurify.common.world.level.structure.StructureSectionClaim;
import com.faboslav.structurify.common.world.level.structure.checks.StructureDistanceFromWorldCenterCheck;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ChunkGenerator.class)
public final class ChunkGeneratorMixin implements StructurifyChunkGenerator
{
	@Shadow
	@Final
	protected BiomeSource biomeSource;

	@Unique
	private final Map<Long, StructureSectionClaim> structurify$structureSectionClaims = new ConcurrentHashMap<>();

	@Unique
	private final Map<Long, StructureChunkResolution> structurify$resolvedStructureChunks = new ConcurrentHashMap<>();

	@Unique
	private final Map<Long, Boolean> structurify$structureChecks = new ConcurrentHashMap<>();

	@Unique
	private final Map<Long, Boolean> structurify$flatnessChecks = new ConcurrentHashMap<>();

	@Unique
	private final Map<Long, Boolean> structurify$biomeChecks = new ConcurrentHashMap<>();

	@Unique
	private final Map<Long, Boolean> structurify$overlapChecks = new ConcurrentHashMap<>();

	@Unique
	public Map<Long, StructureSectionClaim> structurify$getStructureSectionClaims() {
		return this.structurify$structureSectionClaims;
	}

	@Unique
	public Map<Long, StructureChunkResolution> structurify$getResolvedStructureChunks() {
		return this.structurify$resolvedStructureChunks;
	}

	@Unique
	public Map<Long, Boolean> structurify$getStructureChecks() {
		return this.structurify$structureChecks;
	}

	@Unique
	public Map<Long, Boolean> structurify$getFlatnessChecks() {
		return this.structurify$flatnessChecks;
	}

	@Unique
	public Map<Long, Boolean> structurify$getBiomeChecks() {
		return this.structurify$biomeChecks;
	}

	@Unique
	public Map<Long, Boolean> structurify$getOverlapChecks() {
		return this.structurify$overlapChecks;
	}

	@WrapMethod(
		method = "tryGenerateStructure"
	)
	public boolean structurify$trySetStructureStart(
		//? if >=1.21.4 {
		StructureSet.StructureSelectionEntry structureSelectionEntry,
		StructureManager structureManager,
		RegistryAccess registryAccess,
		RandomState randomState,
		StructureTemplateManager structureTemplateManager,
		long seed,
		ChunkAccess chunkAccess,
		ChunkPos chunkPos,
		SectionPos sectionPos,
		ResourceKey<Level> resourceKey,
		Operation<Boolean> original
		//?} else {
		/*StructureSet.StructureSelectionEntry structureSelectionEntry,
		StructureManager structureManager,
		RegistryAccess registryAccess,
		RandomState randomState,
		StructureTemplateManager structureTemplateManager,
		long seed,
		ChunkAccess chunkAccess,
		ChunkPos chunkPos,
		SectionPos sectionPos,
		Operation<Boolean> original
		*///?}
	) {
		if (Structurify.getConfig().disableAllStructures) {
			return false;
		}

		var structureKey = structureSelectionEntry.structure().unwrapKey();

		if (structureKey.isPresent()) {
			Identifier structureName = structureKey.get()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/;
			var structureData = Structurify.getConfig().getStructureData().getOrDefault(structureName.toString(), null);

			if (structureData != null) {
				if (structureData.isDisabled()) {
					return false;
				}

				var distanceFromWorldCenterCheckData = StructureDistanceFromWorldCenterCheck.getDistanceFromWorldCenterData(structureName, structureData);

				if (distanceFromWorldCenterCheckData != null) {
					var distanceFromWorldCenterCheckResult = StructureDistanceFromWorldCenterCheck.checkDistanceFromWorldCenter(distanceFromWorldCenterCheckData, chunkPos);

					if (!distanceFromWorldCenterCheckResult) {
						return false;
					}
				}
			}
		}

		var structurifyStructureSelectionEntry = (StructurifyStructureSelectionEntry) (Object) structureSelectionEntry;
		var structureSet = structurifyStructureSelectionEntry.structurify$getStructureSet();
		var structureSetId = structurifyStructureSelectionEntry.structurify$getStructureSetId();

		if (
			structureSet != null
			&& structureSetId != null
			&& RandomSpreadUtil.getPlacementAttempts(structureSetId) > 1
			&& structureSet.placement() instanceof RandomSpreadStructurePlacement randomSpreadStructurePlacement
		) {
			var context = new StructurePlacementContext(
				(ChunkGenerator) (Object) this,
				registryAccess,
				this.biomeSource,
				randomState,
				structureTemplateManager,
				seed,
				chunkAccess/*? if >= 1.21.4 {*/,
				resourceKey/*?}*/
			);

			ChunkPos resolvedStructureChunk = StructurePlacementResolver.resolveStructureChunk(structureSetId, structureSet, randomSpreadStructurePlacement, chunkPos, context);

			if (!chunkPos.equals(resolvedStructureChunk)) {
				return false;
			}
		}

		//? if >=1.21.4 {
		return original.call(structureSelectionEntry, structureManager, registryAccess, randomState, structureTemplateManager, seed, chunkAccess, chunkPos, sectionPos, resourceKey);
		//?} else {
		/*return original.call(structureSelectionEntry, structureManager, registryAccess, randomState, structureTemplateManager, seed, chunkAccess, chunkPos, sectionPos);
		 *///?}
	}

	@WrapMethod(
		method = "findNearestMapStructure"
	)
	public Pair<BlockPos, Holder<Structure>> structurify$findNearestMapStructure(
		ServerLevel serverLevel,
		HolderSet<Structure> holderSet,
		BlockPos blockPos,
		int i,
		boolean bl,
		Operation<Pair<BlockPos, Holder<Structure>>> original
	) {
		if (Structurify.getConfig().disableAllStructures) {
			return null;
		}

		boolean areAllStructureDisabled = true;

		for (Holder<Structure> holder : holderSet) {
			var structureKey = holder.unwrapKey();

			if (structureKey.isEmpty()) {
				continue;
			}

			String structureName = structureKey.get()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();
			var structureData = Structurify.getConfig().getStructureData().getOrDefault(structureName, null);

			if (structureData == null || !structureData.isDisabled()) {
				areAllStructureDisabled = false;
			}
		}

		if (areAllStructureDisabled) {
			return null;
		}

		return original.call(serverLevel, holderSet, blockPos, i, bl);
	}

	@WrapOperation(
		method = "getNearestGeneratedStructure(Ljava/util/Set;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/level/StructureManager;IIIZJLnet/minecraft/world/level/levelgen/structure/placement/RandomSpreadStructurePlacement;)Lcom/mojang/datafixers/util/Pair;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/chunk/ChunkGenerator;getStructureGeneratingAt(Ljava/util/Set;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/level/StructureManager;ZLnet/minecraft/world/level/levelgen/structure/placement/StructurePlacement;Lnet/minecraft/world/level/ChunkPos;)Lcom/mojang/datafixers/util/Pair;"
		)
	)
	private static Pair<BlockPos, Holder<Structure>> structurify$getStructureGeneratingAtResolvedStructureChunk(
		Set<Holder<Structure>> structures,
		LevelReader levelReader,
		StructureManager structureManager,
		boolean skipReferencedStructures,
		StructurePlacement structurePlacement,
		ChunkPos chunkPos,
		Operation<Pair<BlockPos, Holder<Structure>>> original
	) {
		if (
			structurePlacement instanceof RandomSpreadStructurePlacement randomSpreadStructurePlacement
			&& levelReader instanceof ServerLevel serverLevel
		) {
			var structurifyStructurePlacement = (StructurifyStructurePlacement) structurePlacement;
			var structureSet = structurifyStructurePlacement.structurify$getStructureSet();
			var structureSetId = structurifyStructurePlacement.structurify$getStructureSetId();

			if (
				structureSet != null
				&& structureSetId != null
				&& RandomSpreadUtil.getPlacementAttempts(structureSetId) > 1
			) {
				ChunkPos resolvedStructureChunk = StructurePlacementResolver.resolveStructureChunk(structureSetId, structureSet, randomSpreadStructurePlacement, chunkPos, StructurePlacementContext.of(serverLevel));

				return original.call(structures, levelReader, structureManager, skipReferencedStructures, structurePlacement, resolvedStructureChunk);
			}
		}

		return original.call(structures, levelReader, structureManager, skipReferencedStructures, structurePlacement, chunkPos);
	}

	@WrapMethod(
		method = "getFirstFreeHeight"
	)
	private int structurify$getFirstFreeHeight(
		int x,
		int z,
		Heightmap.Types heightmapType,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		Operation<Integer> original
	) {
		Integer cachedFirstFreeHeight = ChunkGeneratorHeightCache.getFirstFreeHeight((ChunkGenerator) (Object) this, x, z, heightmapType, heightAccessor, randomState);

		if (cachedFirstFreeHeight != null) {
			return cachedFirstFreeHeight;
		}

		int firstFreeHeight = original.call(x, z, heightmapType, heightAccessor, randomState);
		ChunkGeneratorHeightCache.putFirstFreeHeight((ChunkGenerator) (Object) this, x, z, heightmapType, heightAccessor, randomState, firstFreeHeight);

		return firstFreeHeight;
	}

	@WrapMethod(
		method = "getFirstOccupiedHeight"
	)
	private int structurify$getFirstOccupiedHeight(
		int x,
		int z,
		Heightmap.Types heightmapType,
		LevelHeightAccessor heightAccessor,
		RandomState randomState,
		Operation<Integer> original
	) {
		Integer cachedFirstOccupiedHeight = ChunkGeneratorHeightCache.getFirstOccupiedHeight((ChunkGenerator) (Object) this, x, z, heightmapType, heightAccessor, randomState);

		if (cachedFirstOccupiedHeight != null) {
			return cachedFirstOccupiedHeight;
		}

		int firstOccupiedHeight = original.call(x, z, heightmapType, heightAccessor, randomState);
		ChunkGeneratorHeightCache.putFirstOccupiedHeight((ChunkGenerator) (Object) this, x, z, heightmapType, heightAccessor, randomState, firstOccupiedHeight);

		return firstOccupiedHeight;
	}
}
