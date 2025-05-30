package com.faboslav.structurify.common.api;

import net.minecraft.resources.ResourceLocation;

public interface StructurifyStructurePlacement
{
	void structurify$setStructureSetIdentifier(ResourceLocation structureSetIdentifier);

	ResourceLocation structurify$getStructureSetIdentifier();

	int structurify$getOriginalSalt();

	float structurify$getOriginalFrequency();
}
