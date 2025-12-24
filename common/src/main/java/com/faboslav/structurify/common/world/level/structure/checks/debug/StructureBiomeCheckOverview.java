package com.faboslav.structurify.common.world.level.structure.checks.debug;

import com.faboslav.structurify.common.config.data.structure.BiomeCheckData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import java.util.List;

public record StructureBiomeCheckOverview(
	Identifier structureId,
	BoundingBox structureBoundingBox,
	List<StructurePiece> structurePieces,
	BiomeCheckData.BiomeCheckMode biomeCheckMode,
	int checks,
	boolean result
)
{
	@Override
	public String toString() {
		return structureId + "\n" +
			   "Mode: " + biomeCheckMode + "\n" +
			   "Possible checks: " + checks + "\n" +
			   "Result: " + (result ? "success":"fail") + "\n";
	}
}