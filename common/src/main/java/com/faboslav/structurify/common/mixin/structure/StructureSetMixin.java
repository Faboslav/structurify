package com.faboslav.structurify.common.mixin.structure;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyWithStructureSet;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(StructureSet.class)
public abstract class StructureSetMixin implements StructurifyWithStructureSet
{
	@Unique
	@Nullable
	public List<StructureSet.StructureSelectionEntry> structurify$originalStructures = null;

	@Unique
	@Nullable
	public List<StructureSet.StructureSelectionEntry> structurify$structures = null;

	@Unique
	@Nullable
	private String structurify$structureSetId = null;

	public void structurify$setStructureSetId(String structureSetId) {
		this.structurify$structureSetId = structureSetId;
		this.structurify$structures = null;
		this.structurify$originalStructures = null;
	}

	@Nullable
	public String structurify$getStructureSetId() {
		return this.structurify$structureSetId;
	}

	@WrapMethod(
		method = "structures"
	)
	private List<StructureSet.StructureSelectionEntry> structurify$getStructures(Operation<List<StructureSet.StructureSelectionEntry>> originalStructures) {
		var structures = new ArrayList<>(originalStructures.call());

		if (this.structurify$structures == null || (structurify$originalStructures != null && !Objects.equals(this.structurify$originalStructures, structures))) {
			var structureSetId = this.structurify$getStructureSetId();

			if(structureSetId == null || !Structurify.getConfig().getStructureSetData().containsKey(structureSetId)) {
				this.structurify$structures = structures;
				this.structurify$originalStructures = structures;
				return this.structurify$structures;
			}

			List<StructureSet.StructureSelectionEntry> updatedStructures;
			var structureSetData = Structurify.getConfig().getStructureSetData().get(structureSetId);

			if(structureSetData.isDisabled()) {
				updatedStructures = new ArrayList<>();
			} else {
				updatedStructures = new ArrayList<>(structures);

				for (var structureWeight : structureSetData.getStructureWeights().entrySet()) {
					var structureId = structureWeight.getKey();
					var weight = structureWeight.getValue();

					if (weight == 0) {
						updatedStructures.removeIf(entry ->
							entry.structure().unwrapKey()
								.map(structureKey -> {
									String entryStructureId = structureKey/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();
									return entryStructureId.equals(structureId);
								})
								.orElse(false)
						);
					}
				}
			}

			this.structurify$originalStructures = structures;
			this.structurify$structures = updatedStructures;
		}

		return this.structurify$structures;
	}
}