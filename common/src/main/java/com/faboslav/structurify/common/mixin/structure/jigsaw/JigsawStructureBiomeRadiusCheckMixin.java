package com.faboslav.structurify.common.mixin.structure.jigsaw;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

		if (!structureData.isBiomeCheckEnabled()) {
			return original.call(generationContext);
		}

		var biomeCheckDistance = structureData.getBiomeCheckDistance();

		if (biomeCheckDistance != 0 && !(generationContext.biomeSource() instanceof CheckerboardColumnBiomeSource)) {
			ChunkPos chunkPos = generationContext.chunkPos();
			int y = this.startHeight.sample(generationContext.random(), new WorldGenerationContext(generationContext.chunkGenerator(), generationContext.heightAccessor()));
			var blockPos = new BlockPos(generationContext.chunkPos().getMinBlockX(), y, generationContext.chunkPos().getMinBlockZ());

			int sectionY = blockPos.getY();
			sectionY = QuartPos.fromBlock(sectionY);

			for (int curChunkX = chunkPos.x - biomeCheckDistance; curChunkX <= chunkPos.x + biomeCheckDistance; curChunkX++) {
				for (int curChunkZ = chunkPos.z - biomeCheckDistance; curChunkZ <= chunkPos.z + biomeCheckDistance; curChunkZ++) {
					Holder<Biome> biome = generationContext.biomeSource().getNoiseBiome(QuartPos.fromSection(curChunkX), sectionY, QuartPos.fromSection(curChunkZ), generationContext.randomState().sampler());
					if (!generationContext.validBiome().test(biome)) {
						return Optional.empty();
					}
				}
			}
		}

		return original.call(generationContext);
	}
}
