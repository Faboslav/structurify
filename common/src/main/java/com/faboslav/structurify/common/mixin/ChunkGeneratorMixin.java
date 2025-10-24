package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyChunkGenerator;
import com.faboslav.structurify.world.level.structure.StructureSectionClaim;
import com.faboslav.structurify.world.level.structure.checks.DistanceFromWorldCenterCheck;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//? if >=1.21.4 {
/*import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Set;
*///?}

@Mixin(ChunkGenerator.class)
public final class ChunkGeneratorMixin implements StructurifyChunkGenerator
{
	@Unique
	private final Map<Long, StructureSectionClaim> structurify$structureSectionClaims = new ConcurrentHashMap<>();

	@Unique
	public Map<Long, StructureSectionClaim> structurify$getStructureSectionClaims() {
		return this.structurify$structureSectionClaims;
	}

	@WrapMethod(
		method = "tryGenerateStructure"
	)
	public boolean structurify$trySetStructureStart(
		//? if >=1.21.4 {
		/*StructureSet.StructureSelectionEntry structureSelectionEntry,
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
		*///?} else {
		StructureSet.StructureSelectionEntry structureSelectionEntry,
		StructureManager structureManager,
		RegistryAccess registryAccess,
		RandomState randomState,
		StructureTemplateManager structureTemplateManager,
		long seed,
		ChunkAccess chunkAccess,
		ChunkPos chunkPos,
		SectionPos sectionPos,
		Operation<Boolean> original
		//?}
	) {
		if (Structurify.getConfig().disableAllStructures) {
			return false;
		}

		var structureKey = structureSelectionEntry.structure().unwrapKey();

		if (structureKey.isPresent()) {
			ResourceLocation structureName = structureKey.get().location();
			var structureData = Structurify.getConfig().getStructureData().getOrDefault(structureName.toString(), null);

			if (structureData != null) {
				if (structureData.isDisabled()) {
					return false;
				}

				var distanceFromWorldCenterCheckData = DistanceFromWorldCenterCheck.getDistanceFromWorldCenterData(structureName, structureData);
				var distanceFromWorldCenterCheckResult = DistanceFromWorldCenterCheck.checkDistanceFromWorldCenter(distanceFromWorldCenterCheckData, chunkPos);

				if (!distanceFromWorldCenterCheckResult) {
					return false;
				}
			}
		}

		//? if >=1.21.4 {
		/*return original.call(structureSelectionEntry, structureManager, registryAccess, randomState, structureTemplateManager, seed, chunkAccess, chunkPos, sectionPos, resourceKey);
		 *///?} else {
		return original.call(structureSelectionEntry, structureManager, registryAccess, randomState, structureTemplateManager, seed, chunkAccess, chunkPos, sectionPos);
		//?}
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

			String structureName = structureKey.get().location().toString();
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
}
