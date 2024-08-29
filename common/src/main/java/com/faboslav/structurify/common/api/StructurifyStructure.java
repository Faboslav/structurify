package com.faboslav.structurify.common.api;

import net.minecraft.resources.ResourceLocation;

public interface StructurifyStructure
{
	void structurify$setStructureIdentifier(ResourceLocation structureSetIdentifier);

	ResourceLocation structurify$getStructureIdentifier();
}
