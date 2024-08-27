package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkGenerator.class)
public final class ChunkGeneratorMixin
{
	@WrapMethod(
		method = "trySetStructureStart"
	)
	public boolean structurify$trySetStructureStart(
		StructureSet.WeightedEntry weightedEntry,
		StructureAccessor structureAccessor,
		DynamicRegistryManager dynamicRegistryManager,
		NoiseConfig noiseConfig,
		StructureTemplateManager structureManager,
		long seed,
		Chunk chunk,
		ChunkPos pos,
		ChunkSectionPos sectionPos,
		Operation<Boolean> original
	) {
		if (Structurify.getConfig().disableAllStructures) {
			return false;
		}

		String structureName = weightedEntry.structure().getKey().get().getValue().toString();
		var structureData = Structurify.getConfig().getStructureData().getOrDefault(structureName, null);

		if (structureData != null && structureData.isDisabled()) {
			return false;
		}

		return original.call(weightedEntry, structureAccessor, dynamicRegistryManager, noiseConfig, structureManager, seed, chunk, pos, sectionPos);
	}
}
