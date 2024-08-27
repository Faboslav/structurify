package com.faboslav.structurify.common.api;

import net.minecraft.util.Identifier;

public interface StructurifyRandomSpreadStructurePlacement
{
	void structurify$setStructureSetIdentifier(Identifier structureSetIdentifier);

	Identifier structurify$getStructureSetIdentifier();

	int structurify$getOriginalSpacing();

	int structurify$getOriginalSeparation();
}
