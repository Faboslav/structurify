package com.faboslav.structurify.common.mixin.structure.jigsaw;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(JigsawStructure.class)
public interface JigsawStructureAccessor
{
	@Accessor("maxDistanceFromCenter")
	//? if >= 1.21.9 {
	JigsawStructure.MaxDistance structurify$getOriginalMaxDistanceFromCenter();
	//?} else {
	/*int structurify$getOriginalMaxDistanceFromCenter();
	*///?}

	@Accessor("startHeight")
	HeightProvider structurify$getOriginalStartHeight();

	@Accessor("projectStartToHeightmap")
	Optional<Heightmap.Types> structurify$getOriginalProjectStartToHeightmap();

	@Accessor("maxDepth")
	int structurify$getOriginalMaxDepth();

	@Accessor("startPool")
	Holder<StructureTemplatePool> structurify$getOriginalStartPool();
}
