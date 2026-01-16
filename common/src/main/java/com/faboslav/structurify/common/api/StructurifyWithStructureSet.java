package com.faboslav.structurify.common.api;

import org.jetbrains.annotations.Nullable;

public interface StructurifyWithStructureSet
{
	void structurify$setStructureSetId(@Nullable String structureSetId);

	@Nullable
	String structurify$getStructureSetId();
}
