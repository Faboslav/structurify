package com.faboslav.structurify.common.mixin.structure.jigsaw;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.StructureData;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureBiomeRadiusCheckMixin extends Structure implements StructurifyStructure
{
	@Nullable
	public ResourceLocation structureIdentifier = null;

	protected JigsawStructureBiomeRadiusCheckMixin(StructureSettings config) {
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
	private HeightProvider startHeight;

	@WrapMethod(
		method = "findGenerationPoint"
	)
	private Optional<GenerationStub> structurify$getStructurePosition(
		GenerationContext generationContext,
		Operation<Optional<GenerationStub>> original
	) {
		ResourceLocation structureId = structurify$getStructureIdentifier();

		if (
			structureId == null
			|| !Structurify.getConfig().getStructureData().containsKey(structureId.toString())
		) {
			return original.call(generationContext);
		}

		var structureData = Structurify.getConfig().getStructureData().get(structureId.toString());

		Structurify.getLogger().info(String.valueOf(structureData.isFlatnessCheckEnabled()));
		if (structureData.isFlatnessCheckEnabled()) {
			var biomeCheckResult = this.structurify$performFlatnessCheck(structureData, generationContext);

			if(!biomeCheckResult) {
				return Optional.empty();
			}
		}

		if (structureData.isBiomeCheckEnabled()) {
			var biomeCheckResult = this.structurify$performBiomeCheck(structureData, generationContext);

			if(!biomeCheckResult) {
				return Optional.empty();
			}
		}

		return original.call(generationContext);
	}

	private boolean structurify$performBiomeCheck(StructureData structureData, GenerationContext generationContext) {
		var biomeCheckDistance = (int) Math.ceil(structureData.getBiomeCheckDistance() / 16.0);

		if(biomeCheckDistance == 0 || generationContext.biomeSource() instanceof CheckerboardColumnBiomeSource) {
			return true;
		}

		ChunkPos chunkPos = generationContext.chunkPos();
		int y = this.startHeight.sample(generationContext.random(), new WorldGenerationContext(generationContext.chunkGenerator(), generationContext.heightAccessor()));
		var blockPos = new BlockPos(generationContext.chunkPos().getMinBlockX(), y, generationContext.chunkPos().getMinBlockZ());

		int sectionY = QuartPos.fromBlock(blockPos.getY());

		for (int curChunkX = chunkPos.x - biomeCheckDistance; curChunkX <= chunkPos.x + biomeCheckDistance; curChunkX++) {
			for (int curChunkZ = chunkPos.z - biomeCheckDistance; curChunkZ <= chunkPos.z + biomeCheckDistance; curChunkZ++) {
				Holder<Biome> biome = generationContext.biomeSource().getNoiseBiome(QuartPos.fromSection(curChunkX), sectionY, QuartPos.fromSection(curChunkZ), generationContext.randomState().sampler());
				if (!generationContext.validBiome().test(biome)) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean structurify$performFlatnessCheck(StructureData structureData, GenerationContext generationContext) {
		var flatnessCheckDistance = structureData.getFlatnessCheckDistance();
		var offsetStep = (int) Math.ceil(flatnessCheckDistance / 2.0F);
		var flatnessCheckThreshold = structureData.getFlatnessCheckThreshold();

		if(flatnessCheckDistance == 0 || flatnessCheckThreshold == 0) {
			return true;
		}

		ChunkPos chunkPos = generationContext.chunkPos();
		int y = this.startHeight.sample(generationContext.random(), new WorldGenerationContext(generationContext.chunkGenerator(), generationContext.heightAccessor()));
		var blockPos = new BlockPos(chunkPos.getMinBlockX(), y, chunkPos.getMinBlockZ());

		int baseX = blockPos.getX();
		int baseZ = blockPos.getZ();
		int minHeight = Integer.MAX_VALUE;
		int maxHeight = Integer.MIN_VALUE;

		for (int xOffset = -flatnessCheckDistance; xOffset <= flatnessCheckDistance; xOffset += offsetStep) {
			for (int zOffset = -flatnessCheckDistance; zOffset <= flatnessCheckDistance; zOffset += offsetStep) {
				int x = xOffset + baseX;
				int z = zOffset + baseZ;
				int height = generationContext.chunkGenerator().getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, generationContext.heightAccessor(), generationContext.randomState());

				minHeight = Math.min(minHeight, height);
				maxHeight = Math.max(maxHeight, height);

				if (maxHeight - minHeight > flatnessCheckThreshold) {
					return false;
				}
			}
		}

		return true;
	}
}
