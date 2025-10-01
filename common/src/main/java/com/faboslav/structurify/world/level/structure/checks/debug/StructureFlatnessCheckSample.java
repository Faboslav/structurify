package com.faboslav.structurify.world.level.structure.checks.debug;

import net.minecraft.resources.ResourceLocation;

public record StructureFlatnessCheckSample(
	ResourceLocation structureId,
	int x,
	int z,
	int occY,
	int freeY,
	boolean isSolid
) {
}