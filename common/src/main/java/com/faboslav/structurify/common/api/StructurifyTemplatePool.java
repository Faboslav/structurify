package com.faboslav.structurify.common.api;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

public interface StructurifyTemplatePool
{
	void structurify$setStructureTemplatePoolId(@Nullable String structureTemplatePoolId);

	@Nullable
	String structurify$getStructureTemplatePoolId();
}
