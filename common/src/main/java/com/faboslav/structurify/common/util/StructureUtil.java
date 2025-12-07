package com.faboslav.structurify.common.util;

import net.minecraft.world.level.levelgen.GenerationStep;

public final class StructureUtil
{
	public static boolean isUndergroundStructure(GenerationStep.Decoration structureStep) {
		if (structureStep == GenerationStep.Decoration.SURFACE_STRUCTURES || structureStep == GenerationStep.Decoration.LOCAL_MODIFICATIONS) {
			return false;
		}

		return true;
	}
}
