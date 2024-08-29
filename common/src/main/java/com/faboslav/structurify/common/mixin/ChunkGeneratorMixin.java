package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkGenerator.class)
public final class ChunkGeneratorMixin
{
	@WrapMethod(
		method = "tryGenerateStructure"
	)
	public boolean structurify$trySetStructureStart(
		StructureSet.StructureSelectionEntry weightedEntry,
		StructureManager structureAccessor,
		RegistryAccess dynamicRegistryManager,
		RandomState noiseConfig,
		StructureTemplateManager structureManager,
		long seed,
		ChunkAccess chunk,
		ChunkPos pos,
		SectionPos sectionPos,
		Operation<Boolean> original
	) {
		if (Structurify.getConfig().disableAllStructures) {
			return false;
		}

		String structureName = weightedEntry.structure().unwrapKey().get().location().toString();
		var structureData = Structurify.getConfig().getStructureData().getOrDefault(structureName, null);

		if (structureData != null && structureData.isDisabled()) {
			return false;
		}

		return original.call(weightedEntry, structureAccessor, dynamicRegistryManager, noiseConfig, structureManager, seed, chunk, pos, sectionPos);
	}
}
