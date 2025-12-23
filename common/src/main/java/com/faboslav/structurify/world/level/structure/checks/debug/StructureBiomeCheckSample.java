package com.faboslav.structurify.world.level.structure.checks.debug;

import net.minecraft.resources.Identifier;

public record StructureBiomeCheckSample(
	Identifier structureId,
	int x,
	int y,
	int z,
	Identifier biome,
	boolean result
)
{
}