package com.faboslav.structurify.common.api;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface StructurifyStructurePlacement
{
	void structurify$setStructureSetIdentifier(@Nullable ResourceLocation structureSetIdentifier);

	@Nullable
	ResourceLocation structurify$getStructureSetIdentifier();

	int structurify$getOriginalSalt();

	float structurify$getOriginalFrequency();
}
