package com.faboslav.structurify.world.level.structure.checks.debug;

import net.minecraft.resources.ResourceLocation;

public record StructureBiomeCheckSample(
	ResourceLocation structureId,
	int x,
	int y,
	int z,
	ResourceLocation biome,
	boolean result
)
{
}