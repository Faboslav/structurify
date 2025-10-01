package com.faboslav.structurify.world.level.structure.checks;

import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record StructureBiomeCheckStructurePiece(BoundingBox boundingBox, int chebyshevDistBlocks, int groupSizeQuarts) {

}