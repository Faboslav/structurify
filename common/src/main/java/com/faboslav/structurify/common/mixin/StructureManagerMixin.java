package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.world.level.structure.checks.StructureChecker;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StructureManager.class)
public abstract class StructureManagerMixin
{
	@Shadow
	@Final
	private LevelAccessor level;

	@Shadow
	public abstract void setStartForStructure(
		SectionPos sectionPos,
		Structure structure,
		StructureStart structureStart,
		StructureAccess structureAccess
	);

	@Nullable
	@WrapMethod(
		method = "getStartForStructure"
	)
	public StructureStart structurify$getStartForStructure(
		SectionPos sectionPos,
		Structure structure,
		StructureAccess structureAccess,
		Operation<StructureStart> original
	) {
		var structureStart = original.call(sectionPos, structure, structureAccess);
		StructurifyStructure structurifyStructure = (StructurifyStructure) structure;
		Identifier structureId = structurifyStructure.structurify$getStructureIdentifier();

		if (structureStart == null || structureStart == StructureStart.INVALID_START || !structureStart.isValid()) {
			return structureStart;
		}

		ServerLevel serverLevel = null;

		if(this.level instanceof ServerLevel) {
			serverLevel = (ServerLevel) this.level;
		} else if(this.level instanceof WorldGenRegion) {
			serverLevel = ((WorldGenRegion) this.level).getLevel();
		}

		if(!(serverLevel instanceof ServerLevel)) {
			return structureStart;
		}

		var chunkSource = serverLevel.getChunkSource();
		var chunkGenerator = chunkSource.getGenerator();
		var biomeSource = chunkGenerator.getBiomeSource();
		var randomState = chunkSource.randomState();

		var structureCheckResult = StructureChecker.checkStructure(structureStart, structureId, structurifyStructure, chunkGenerator, serverLevel, randomState, biomeSource);

		if (!structureCheckResult) {
			this.setStartForStructure(sectionPos, structure, StructureStart.INVALID_START, structureAccess);
			return StructureStart.INVALID_START;
		}

		return structureStart;
	}
}
