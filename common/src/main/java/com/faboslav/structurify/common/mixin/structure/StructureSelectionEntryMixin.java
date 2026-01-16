package com.faboslav.structurify.common.mixin.structure;

import com.faboslav.structurify.common.api.StructurifyStructureSelectionEntry;
import com.faboslav.structurify.common.util.RandomSpreadUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureSet.StructureSelectionEntry.class)
public abstract class StructureSelectionEntryMixin implements StructurifyStructureSelectionEntry
{
	@Unique
	@Nullable
	public String structurify$structureId = null;

	public void structurify$setStructureSetId(String structureSetId) {
		this.structurify$structureId = structureSetId;
	}

	@Nullable
	public String structurify$getStructureSetId() {
		return this.structurify$structureId;
	}

	@Shadow
	@Final
	private Holder<Structure> structure;

	@Shadow
	@Final
	private int weight;

	public int structurify$getOriginalWeight() {
		return this.weight;
	}

	@ModifyReturnValue(
		method = "weight",
		at = @At("RETURN")
	)
	private int structurify$getWeight(int originalWeight) {
		String structureId = this.structure.unwrapKey().get()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();
		return RandomSpreadUtil.getModifiedStructureWeight(this.structurify$getStructureSetId(), structureId, originalWeight);
	}
}