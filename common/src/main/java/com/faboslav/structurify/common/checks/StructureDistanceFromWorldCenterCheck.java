package com.faboslav.structurify.common.checks;

import net.minecraft.core.BlockPos;

/**
 * Inspired by use in Repurposed Structures mod
 *
 * @author TelepathicGrunt
 * <a href="https://github.com/TelepathicGrunt/RepurposedStructures/blob/1.21-Arch/common/src/main/java/com/telepathicgrunt/repurposedstructures/world/placements/MinDistanceFromWorldOriginPlacement.java">https://github.com/TelepathicGrunt/RepurposedStructures/blob/1.21-Arch/common/src/main/java/com/telepathicgrunt/repurposedstructures/world/placements/MinDistanceFromWorldOriginPlacement.java</a>
 */
public final class StructureDistanceFromWorldCenterCheck
{
	public static boolean checkStructureDistanceFromWorldCenter(BlockPos structureBlockPos, int minStructureDistanceFromWorldCenter) {
		return structureBlockPos.distManhattan(BlockPos.ZERO) >= minStructureDistanceFromWorldCenter;
	}
}
