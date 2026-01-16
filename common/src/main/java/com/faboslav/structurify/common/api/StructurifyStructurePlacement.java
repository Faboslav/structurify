package com.faboslav.structurify.common.api;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public interface StructurifyStructurePlacement extends StructurifyWithStructureSet
{
	int structurify$getOriginalSalt();

	float structurify$getOriginalFrequency();
}
