package com.faboslav.structurify.common.mixin.structure;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructureTemplatePool.class)
public interface StructureTemplatePoolMixin
{
	@Accessor
	List<Pair<StructurePoolElement, Integer>> getRawTemplates();

	@Mutable
	@Accessor
	void setRawTemplates(List<Pair<StructurePoolElement, Integer>> rawTemplates);

	@Accessor
	ObjectArrayList<StructurePoolElement> getTemplates();

	@Mutable
	@Accessor
	void setTemplates(ObjectArrayList<StructurePoolElement> templates);

	@Accessor
	int getMaxSize();

	@Accessor
	void setMaxSize(int maxSize);
}
