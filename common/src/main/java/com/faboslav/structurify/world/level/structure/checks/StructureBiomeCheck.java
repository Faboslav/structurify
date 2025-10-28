package com.faboslav.structurify.world.level.structure.checks;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureBiomeCheckOverview;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureBiomeCheckSample;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.Nullable;

public final class StructureBiomeCheck
{
	@Nullable
	public static BiomeCheckData getBiomeCheckData(
		StructureCheckData structureCheckData
	) {
		var structure = structureCheckData.getStructure();
		var structureId = structureCheckData.getStructureId();
		var globalNamespaceData = structure.structurify$getGlobalStructureNamespaceData();
		var structureNamespaceData = structure.structurify$getStructureNamespaceData(structureId);
		var structureData = structure.structurify$getStructureData(structureId);

		@Nullable
		BiomeCheckData biomeCheckDataToCheck = null;

		if(globalNamespaceData != null) {
			biomeCheckDataToCheck = globalNamespaceData.getBiomeCheckData();;
		}

		if(structureNamespaceData != null) {
			var namespaceBiomeCheckData = structureNamespaceData.getBiomeCheckData();

			if (namespaceBiomeCheckData.isOverridingGlobalBiomeCheck() || namespaceBiomeCheckData.isEnabled()) {
				biomeCheckDataToCheck = namespaceBiomeCheckData;
			}
		}

		if(structureData != null) {
			var structureBiomeCheckData = structureData.getBiomeCheckData();

			if (structureBiomeCheckData.isOverridingGlobalBiomeCheck() || structureBiomeCheckData.isEnabled()) {
				biomeCheckDataToCheck = structureBiomeCheckData;
			}
		}

		return biomeCheckDataToCheck;
	}


	public static boolean checkBiomes(
		StructureCheckData structureCheckData,
		BiomeCheckData biomeCheckData,
		BiomeSource biomeSource,
		RandomState randomState
	) {
		StructurifyStructure structure = structureCheckData.getStructure();
		HolderSet<Biome> allowedBiomes = structure.structurify$getStructureBiomes();
		HolderSet<Biome> blacklistedBiomes = structure.structurify$getStructureBlacklistedBiomes();
		BiomeCheckData.BiomeCheckMode mode = biomeCheckData.getMode();

		if (mode == BiomeCheckData.BiomeCheckMode.BLACKLIST && biomeCheckData.getBlacklistedBiomes().isEmpty()) {
			return true;
		}

		int blockY = structureCheckData.getStructureCenter().getY();
		int sampleQuartY = QuartPos.fromBlock(blockY);
		var sampler = randomState.sampler();

		for (int[] pos : structureCheckData.getStructurePieceSamples()) {
			int blockX = pos[0];
			int blockZ = pos[1];

			int quartX = QuartPos.fromBlock(blockX);
			int quartZ = QuartPos.fromBlock(blockZ);

			Holder<Biome> biome = biomeSource.getNoiseBiome(quartX, sampleQuartY, quartZ, sampler);

			if (mode == BiomeCheckData.BiomeCheckMode.STRICT) {
				if (allowedBiomes != null && !allowedBiomes.contains(biome)) {
					debugAddStructureBiomeCheckSample(structureCheckData, blockX, blockY, blockZ, biome, false);
					debugAddStructureBiomeCheckOverview(structureCheckData, false);
					return false;
				}
			} else if (mode == BiomeCheckData.BiomeCheckMode.BLACKLIST) {
				if (blacklistedBiomes != null && blacklistedBiomes.contains(biome)) {
					debugAddStructureBiomeCheckSample(structureCheckData, blockX, blockY, blockZ, biome, false);
					debugAddStructureBiomeCheckOverview(structureCheckData, false);
					return false;
				}
			}

			debugAddStructureBiomeCheckSample(structureCheckData, blockX, blockY, blockZ, biome, true);
		}

		debugAddStructureBiomeCheckOverview(structureCheckData, true);
		return true;
	}

	private static void debugAddStructureBiomeCheckOverview(
		StructureCheckData structureCheckData,
		boolean result
	) {
		Structurify.getConfig().getDebugData().addStructureBiomeCheckOverview(
			ChunkPos.asLong(structureCheckData.getStructureCenter()),
			new StructureBiomeCheckOverview(
				structureCheckData.getStructureId(),
				structureCheckData.getStructureStart().getBoundingBox(),
				structureCheckData.getStructurePieces(),
				structureCheckData.getStructure().structurify$getStructureData().getBiomeCheckData().getMode(),
				structureCheckData.getStructurePieceSamples().length,
				result
			)
		);
	}

	private static void debugAddStructureBiomeCheckSample(
		StructureCheckData structureCheckData,
		int x,
		int y,
		int z,
		Holder<Biome> biome,
		boolean result
	) {
		Structurify.getConfig().getDebugData().addStructureBiomeCheckSample(
			ChunkPos.asLong(structureCheckData.getStructureCenter()),
			new StructureBiomeCheckSample(
				structureCheckData.getStructureId(),
				x,
				y,
				z,
				biome.unwrapKey().get().location(),
				result
			)
		);
	}
}