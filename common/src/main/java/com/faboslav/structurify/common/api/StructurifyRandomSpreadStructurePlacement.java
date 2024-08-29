package com.faboslav.structurify.common.api;

import net.minecraft.resources.ResourceLocation;

public interface StructurifyRandomSpreadStructurePlacement
{
	void structurify$setStructureSetIdentifier(ResourceLocation structureSetIdentifier);

	ResourceLocation structurify$getStructureSetIdentifier();

	int structurify$getOriginalSpacing();

	int structurify$getOriginalSeparation();
}
