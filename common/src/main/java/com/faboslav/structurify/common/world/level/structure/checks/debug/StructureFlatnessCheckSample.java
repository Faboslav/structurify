package com.faboslav.structurify.common.world.level.structure.checks.debug;

import net.minecraft.resources.Identifier;

public record StructureFlatnessCheckSample(
	Identifier structureId,
	int x,
	int z,
	int occY,
	int freeY,
	boolean isSolid
)
{
}