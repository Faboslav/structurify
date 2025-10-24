package com.faboslav.structurify.common.mixin.structure.jigsaw;

import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JigsawStructure.class)
public interface JigsawStructureAccessor
{
	@Accessor("maxDistanceFromCenter")
	//? if >= 1.21.9 {
	/*JigsawStructure.MaxDistance structurify$getMaxDistanceFromCenter();
	*///?} else {
	int structurify$getMaxDistanceFromCenter();
	//?}

	@Accessor("maxDepth")
	int structurify$getMaxDepth();
}
