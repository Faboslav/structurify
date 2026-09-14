package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.world.level.structure.StructurePlacementResolver;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkGeneratorStructureState.class)
public abstract class ChunkGeneratorStructureStateMixin
{
	@WrapMethod(
		method = "hasStructureChunkInRange"
	)
	private boolean structurify$hasStructureChunkInRange(
		Holder<StructureSet> structureSet,
		int sourceX,
		int sourceZ,
		int range,
		Operation<Boolean> original
	) {
		boolean previousUseOriginalPlacementChunksOnly = StructurePlacementResolver.shouldUseOriginalPlacementChunksOnly();
		StructurePlacementResolver.setUseOriginalPlacementChunksOnly(true);

		try {
			return original.call(structureSet, sourceX, sourceZ, range);
		} finally {
			StructurePlacementResolver.setUseOriginalPlacementChunksOnly(previousUseOriginalPlacementChunksOnly);
		}
	}
}
