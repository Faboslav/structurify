package com.faboslav.structurify.common.api;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface StructurifyTemplatePool
{
	List<Pair<StructurePoolElement, Integer>> structurify$getOriginalRawTemplates();

	List<Pair<StructurePoolElement, Integer>> structurify$getRawTemplates();

	void structurify$setRawTemplates(List<Pair<StructurePoolElement, Integer>> rawTemplates);

	void structurify$setStructureTemplatePoolId(@Nullable String structureTemplatePoolId);

	@Nullable
	String structurify$getStructureTemplatePoolId();
}
