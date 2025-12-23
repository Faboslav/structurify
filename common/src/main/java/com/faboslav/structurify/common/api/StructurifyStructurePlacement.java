package com.faboslav.structurify.common.api;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public interface StructurifyStructurePlacement
{
	void structurify$setStructureSetIdentifier(@Nullable Identifier structureSetIdentifier);

	@Nullable
	Identifier structurify$getStructureSetIdentifier();

	int structurify$getOriginalSalt();

	float structurify$getOriginalFrequency();
}
