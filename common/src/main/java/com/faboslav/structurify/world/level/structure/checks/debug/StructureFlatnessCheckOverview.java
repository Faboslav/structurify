package com.faboslav.structurify.world.level.structure.checks.debug;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

import java.util.List;

public record StructureFlatnessCheckOverview(
	ResourceLocation structureId,
	BoundingBox structureBoundingBox,
	List<StructurePiece> structurePieces,
	int structureArea,
	int minHeight,
	int maxHeight,
	int flatnessCheckThreshold,
	int nonSolidChecks,
	int failedNonSolidChecks,
	int nonSolidFlatnessChecksThreshold,
	boolean result
) {
	@Override
	public String toString() {
		return structureId + "\n" +
			   "Pieces: " + structurePieces.size() + " (area: " + structureArea + ")\n" +
			   "Height threshold: " + flatnessCheckThreshold + " (min Y: " + minHeight + ", max Y: " + maxHeight + ")\n" +
			   "Non solid checks: " + nonSolidChecks + "\n" +
			   "Failed checks: " + failedNonSolidChecks + "/" + nonSolidFlatnessChecksThreshold + "\n" +
			   "Result: " + (result ? "success" : "fail");
	}
}