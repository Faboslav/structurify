package com.faboslav.structurify.common.util;

import com.faboslav.structurify.common.mixin.structure.pools.SinglePoolElementAccessor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public class StructurePieceUtil
{
	public static String getStructurePieceName(StructurePiece structurePiece) {
		String pieceName = "";

		if (structurePiece instanceof PoolElementStructurePiece poolPiece) {
			StructurePoolElement element = poolPiece.getElement();

			if (element instanceof SinglePoolElement single) {
				Identifier template = ((SinglePoolElementAccessor) single).getTemplate().left().orElse(null);

				if (template != null) {
					pieceName = template.toString();
				}
			} else {
				pieceName = "";
			}
		}

		return pieceName;
	}
}
