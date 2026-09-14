package com.faboslav.structurify.common.api;

import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.jetbrains.annotations.Nullable;

public interface StructurifyStructureSelectionEntry extends StructurifyWithStructureSet
{
	int structurify$getOriginalWeight();

	void structurify$setStructureSet(@Nullable StructureSet structureSet);

	@Nullable
	StructureSet structurify$getStructureSet();
}
