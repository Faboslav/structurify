package com.faboslav.structurify.common.mixin.structure.jigsaw;

import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JigsawStructure.class)
public interface MaxDistanceFromCenterAccessor
{
	@Accessor("maxDistanceFromCenter")
	int structurify$getMaxDistanceFromCenter();
}
