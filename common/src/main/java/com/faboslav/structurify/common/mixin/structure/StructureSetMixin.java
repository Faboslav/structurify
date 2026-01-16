package com.faboslav.structurify.common.mixin.structure;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyWithStructureSet;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(StructureSet.class)
public abstract class StructureSetMixin implements StructurifyWithStructureSet
{
	@Shadow
	@Final
	private List<StructureSet.StructureSelectionEntry> structures;

	@Unique
	@Nullable
	public List<StructureSet.StructureSelectionEntry> structurify$structures = null;

	@Unique
	@Nullable
	private String structurify$structureId = null;

	public void structurify$setStructureSetId(String structureSetId) {
		this.structurify$structureId = structureSetId;
		this.structurify$structures = null;
	}

	@Nullable
	public String structurify$getStructureSetId() {
		return this.structurify$structureId;
	}

	@WrapMethod(
		method = "structures"
	)
	private List<StructureSet.StructureSelectionEntry> structurify$getStructures(Operation<List<StructureSet.StructureSelectionEntry>> original) {
		if (this.structurify$structures == null) {
			var structureSetId = this.structurify$getStructureSetId();

			if(structureSetId == null || !Structurify.getConfig().getStructureSetData().containsKey(structureSetId)) {
				return original.call();
			}

			var structureSetData = Structurify.getConfig().getStructureSetData().get(structureSetId);
			var originalStructures = new ArrayList<>(original.call());

			for(var structureWeight : structureSetData.getStructureWeights().entrySet()) {
				var structureId = structureWeight.getKey();
				var weight = structureWeight.getValue();

				if (weight == 0) {
					Structurify.getLogger().info("Structure weight for structure set {} is currently 0, structure {} will be removed.", structureSetId, structureId);
					originalStructures.removeIf(e -> e.structure().getRegisteredName().equals(structureId));
				}
			}

			this.structurify$structures = originalStructures;
		}

		return structurify$structures;
	}
}