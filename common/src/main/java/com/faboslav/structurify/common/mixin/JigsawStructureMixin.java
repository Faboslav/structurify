package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.RandomSpreadStructurePlacement;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.StructureData;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMixin extends Structure implements StructurifyStructure
{
	@Nullable
	public Identifier structureIdentifier = null;

	protected JigsawStructureMixin(Config config) {
		super(config);
	}

	public void structurify$setStructureIdentifier(Identifier structureIdentifier) {
		this.structureIdentifier = structureIdentifier;
	}

	@Nullable
	public Identifier structurify$getStructureIdentifier() {
		return this.structureIdentifier;
	}

	@Shadow
	@Final
	private int maxDistanceFromCenter;

	@Inject(
		method = "getStructurePosition",
		at = @At(value = "TAIL"),
		cancellable = true
	)
	private void structurify$getStructurePosition(
		Structure.Context context,
		CallbackInfoReturnable<Optional<Structure.StructurePosition>> cir
	) {
		Identifier structureId = structurify$getStructureIdentifier();

		if (structureId != null) {
			if(Structurify.getConfig().getStructureData().containsKey(structureId.toString())) {
				List<String> blacklistedBiomeIds = Structurify.getConfig().getStructureData().get(structureId.toString()).getBlacklistedBiomes();

				if(!blacklistedBiomeIds.isEmpty()) {
					Set<RegistryKey<Biome>> blacklistedBiomeKeys = blacklistedBiomeIds.stream()
						.map(id -> RegistryKey.of(RegistryKeys.BIOME, new Identifier(id)))
						.collect(Collectors.toSet());

					Predicate<RegistryEntry<Biome>> blackListedBiomesPredicate = biomeEntry -> {
						return blacklistedBiomeKeys.contains(biomeEntry.getKey().orElse(null));
					};

					BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 63, context.chunkPos().getStartZ());
					var biomeBlacklistType = Structurify.getConfig().getStructureData().get(structureId.toString()).getBiomeBlacklistType();

					if(biomeBlacklistType == StructureData.BiomeBlacklistType.CENTER_PART) {
						var structurePosition = new Structure.StructurePosition(blockPos, collector -> {});
						var isBiomeBlacklisted = this.structurify$isBiomeValid(structurePosition, context.chunkGenerator(), context.noiseConfig(), blackListedBiomesPredicate);

						if (isBiomeBlacklisted) {
							cir.setReturnValue(Optional.empty());
						}
					} else if(biomeBlacklistType == StructureData.BiomeBlacklistType.ALL_PARTS) {
						int checkRadius = this.maxDistanceFromCenter;
						int stepSize = 8;

						for (int xOffset = -checkRadius; xOffset <= checkRadius; xOffset += stepSize) {
							for (int zOffset = -checkRadius; zOffset <= checkRadius; zOffset += stepSize) {
								int x = xOffset + blockPos.getX();
								int y = blockPos.getY();
								int z = zOffset + blockPos.getZ();

								var structurePosition = new Structure.StructurePosition(new BlockPos(x, y, z), collector -> {});
								var isBiomeBlacklisted = this.structurify$isBiomeValid(structurePosition, context.chunkGenerator(), context.noiseConfig(), blackListedBiomesPredicate);

								if (isBiomeBlacklisted) {
									cir.setReturnValue(Optional.empty());
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean structurify$isBiomeValid(
		Structure.StructurePosition result,
		ChunkGenerator chunkGenerator,
		NoiseConfig noiseConfig,
		Predicate<RegistryEntry<Biome>> validBiomes
	) {
		BlockPos blockPos = result.position();
		return validBiomes.test(chunkGenerator.getBiomeSource().getBiome(BiomeCoords.fromBlock(blockPos.getX()), BiomeCoords.fromBlock(blockPos.getY()), BiomeCoords.fromBlock(blockPos.getZ()), noiseConfig.getMultiNoiseSampler()));
	}
}
