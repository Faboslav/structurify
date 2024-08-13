package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerator.class)
public final class ChunkGeneratorMixin
{
	@Inject(method = "trySetStructureStart", at = @At("HEAD"), cancellable = true)
	public void structurify$trySetStructureStart(
		StructureSet.WeightedEntry weightedEntry,
		StructureAccessor structureAccessor,
		DynamicRegistryManager dynamicRegistryManager,
		NoiseConfig noiseConfig,
		StructureTemplateManager structureManager,
		long seed,
		Chunk chunk,
		ChunkPos pos,
		ChunkSectionPos sectionPos,
		CallbackInfoReturnable<Boolean> cir
	) {
		if(Structurify.getConfig().disableAllStructures) {
			cir.setReturnValue(false);
		}

		String structureName = weightedEntry.structure().getKey().get().getValue().toString();
		var structureData = Structurify.getConfig().getStructureData().getOrDefault(structureName, null);

		if (structureData != null && structureData.isDisabled()) {
			// TODO handle debug?
			// Structurized.getLogger().info("Disabled generation of {}", weightedEntry.structure().getKey().get().getValue().toString());
			cir.setReturnValue(false);
		}
	}
}
