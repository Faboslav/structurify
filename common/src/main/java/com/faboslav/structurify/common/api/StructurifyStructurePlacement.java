package com.faboslav.structurify.common.api;

import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.jetbrains.annotations.Nullable;

public interface StructurifyStructurePlacement extends StructurifyWithStructureSet
{
	int structurify$getOriginalSalt();

	float structurify$getOriginalFrequency();

	void structurify$setStructureSet(@Nullable StructureSet structureSet);

	@Nullable
	StructureSet structurify$getStructureSet();
}
