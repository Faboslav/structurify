package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.checks.StructureDistanceFromWorldCenterCheck;
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

//? >=1.21.4 {
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
//?}

@Mixin(ChunkGenerator.class)
public final class ChunkGeneratorMixin
{
	@WrapMethod(
		method = "tryGenerateStructure"
	)
	public boolean structurify$trySetStructureStart(
		//? >=1.21.4 {
		StructureSet.StructureSelectionEntry structureSelectionEntry,
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
		//?} else {
		/*StructureSet.StructureSelectionEntry structureSelectionEntry,
		StructureManager structureManager,
		RegistryAccess registryAccess,
		RandomState randomState,
		StructureTemplateManager structureTemplateManager,
		long seed,
		ChunkAccess chunkAccess,
		ChunkPos chunkPos,
		SectionPos sectionPos,
		Operation<Boolean> original
		*///?}
	) {
		if (Structurify.getConfig().disableAllStructures) {
			return false;
		}

		var minStructureDistanceFromWorldCenter = Structurify.getConfig().minStructureDistanceFromWorldCenter;

		if (minStructureDistanceFromWorldCenter > 0) {
			var checkStructureDistanceFromWorldCenterResult = StructureDistanceFromWorldCenterCheck.checkStructureDistanceFromWorldCenter(chunkPos.getWorldPosition(), minStructureDistanceFromWorldCenter);

			if (!checkStructureDistanceFromWorldCenterResult) {
				return false;
			}
		}

		String structureName = structureSelectionEntry.structure().unwrapKey().get().location().toString();
		var structureData = Structurify.getConfig().getStructureData().getOrDefault(structureName, null);

		if (structureData != null && structureData.isDisabled()) {
			return false;
		}

		//? >=1.21.4 {
		return original.call(structureSelectionEntry, structureManager, registryAccess, randomState, structureTemplateManager, seed, chunkAccess, chunkPos, sectionPos, resourceKey);
		//?} else {
		/*return original.call(structureSelectionEntry, structureManager, registryAccess, randomState, structureTemplateManager, seed, chunkAccess, chunkPos, sectionPos);
		 *///?}
	}
}
