package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.StructureData;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMixin extends Structure implements StructurifyStructure
{
	@Nullable
	public ResourceLocation structureIdentifier = null;

	protected JigsawStructureMixin(StructureSettings config) {
		super(config);
	}

	public void structurify$setStructureIdentifier(ResourceLocation structureIdentifier) {
		this.structureIdentifier = structureIdentifier;
	}

	@Nullable
	public ResourceLocation structurify$getStructureIdentifier() {
		return this.structureIdentifier;
	}

	@Shadow
	@Final
	private int maxDistanceFromCenter;

	@WrapMethod(
		method = "findGenerationPoint"
	)
	private Optional<GenerationStub> structurify$getStructurePosition(
		GenerationContext context,
		Operation<Optional<GenerationStub>> original
	) {
		var originalStructurePosition = original.call(context);
		ResourceLocation structureId = structurify$getStructureIdentifier();

		if (structureId != null) {
			if (Structurify.getConfig().getStructureData().containsKey(structureId.toString())) {
				List<String> blacklistedBiomeIds = Structurify.getConfig().getStructureData().get(structureId.toString()).getBlacklistedBiomes();

				if (!blacklistedBiomeIds.isEmpty()) {
					Set<ResourceKey<Biome>> blacklistedBiomeKeys = blacklistedBiomeIds.stream()
						.map(id -> ResourceKey.create(Registries.BIOME, Structurify.makeVanillaId(id)))
						.collect(Collectors.toSet());

					Predicate<Holder<Biome>> blackListedBiomesPredicate = biomeEntry -> {
						return blacklistedBiomeKeys.contains(biomeEntry.unwrapKey().orElse(null));
					};

					var blockPos = new BlockPos(context.chunkPos().getMinBlockX(), 63, context.chunkPos().getMinBlockZ());
					var biomeBlacklistType = Structurify.getConfig().getStructureData().get(structureId.toString()).getBiomeBlacklistType();

					if (biomeBlacklistType == StructureData.BiomeBlacklistType.CENTER_PART) {
						var structurePosition = new Structure.GenerationStub(blockPos, collector -> {
						});
						var isBiomeBlacklisted = this.structurify$isBiomeValid(structurePosition, context.chunkGenerator(), context.randomState(), blackListedBiomesPredicate);

						if (isBiomeBlacklisted) {
							return Optional.empty();
						}
					} else if (biomeBlacklistType == StructureData.BiomeBlacklistType.ALL_PARTS) {
						int checkRadius = this.maxDistanceFromCenter;
						int stepSize = 8;

						for (int xOffset = -checkRadius; xOffset <= checkRadius; xOffset += stepSize) {
							for (int zOffset = -checkRadius; zOffset <= checkRadius; zOffset += stepSize) {
								int x = xOffset + blockPos.getX();
								int y = blockPos.getY();
								int z = zOffset + blockPos.getZ();

								var structurePosition = new Structure.GenerationStub(new BlockPos(x, y, z), collector -> {
								});
								var isBiomeBlacklisted = this.structurify$isBiomeValid(structurePosition, context.chunkGenerator(), context.randomState(), blackListedBiomesPredicate);

								if (isBiomeBlacklisted) {
									return Optional.empty();
								}
							}
						}
					}
				}
			}
		}

		return originalStructurePosition;
	}

	private boolean structurify$isBiomeValid(
		Structure.GenerationStub result,
		ChunkGenerator chunkGenerator,
		RandomState noiseConfig,
		Predicate<Holder<Biome>> validBiomes
	) {
		var blockPos = result.position();
		return validBiomes.test(chunkGenerator.getBiomeSource().getNoiseBiome(QuartPos.fromBlock(blockPos.getX()), QuartPos.fromBlock(blockPos.getY()), QuartPos.fromBlock(blockPos.getZ()), noiseConfig.sampler()));
	}
}
