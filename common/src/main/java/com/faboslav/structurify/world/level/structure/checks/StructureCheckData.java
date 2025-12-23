package com.faboslav.structurify.world.level.structure.checks;

import com.faboslav.structurify.common.api.StructurifyStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayList;
import java.util.List;

public final class StructureCheckData
{
	private final Identifier structureId;
	private final StructurifyStructure structure;
	private final StructureStart structureStart;
	private final BlockPos structureCenter;

	private List<StructurePiece> structurePieces = new ArrayList<>();
	private int[][] structurePieceSamples = null;

	public StructureCheckData(
		Identifier structureId,
		StructurifyStructure structure,
		StructureStart structureStart
	) {
		this.structureId = structureId;
		this.structureStart = structureStart;
		this.structure = structure;
		this.structureCenter = structureStart.getBoundingBox().getCenter();
	}

	public List<StructurePiece> getStructurePieces() {
		if (this.structurePieces.isEmpty()) {
			this.setStructureCheckData();
		}

		return this.structurePieces;
	}

	public int[][] getStructurePieceSamples() {
		if (this.structurePieceSamples == null) {
			this.setStructureCheckData();
		}

		return this.structurePieceSamples;
	}

	public Identifier getStructureId() {
		return this.structureId;
	}

	public StructurifyStructure getStructure() {
		return this.structure;
	}

	public StructureStart getStructureStart() {
		return this.structureStart;
	}

	public BlockPos getStructureCenter() {
		return this.structureCenter;
	}

	private void setStructureCheckData() {
		this.structurePieces = StructurePieceSampler.getStructurePieces(this.structureStart);
		this.structurePieceSamples = StructurePieceSampler.getStructurePieceSamples(this.structurePieces, this.structureCenter);
	}
}
