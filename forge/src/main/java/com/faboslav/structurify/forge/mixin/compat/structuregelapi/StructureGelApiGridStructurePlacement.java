//? if structure_gel_api {
package com.faboslav.structurify.forge.mixin.compat.structuregelapi;

import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.util.RandomSpreadUtil;
import com.legacy.structure_gel.api.structure.GridStructurePlacement;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(value = GridStructurePlacement.class, remap = false)
public abstract class StructureGelApiGridStructurePlacement implements StructurifyRandomSpreadStructurePlacement
{
	@Shadow
	@Final
	private int spacing;

	@Shadow
	@Final
	private int offset;

	@Unique
	@Nullable
	private String structurify$structureSetId = null;

	@Shadow
	@Final
	public abstract int spacing();

	@Shadow
	@Final
	public abstract int offset();

	public void structurify$setStructureSetId(String structureSetId) {
		this.structurify$structureSetId = structureSetId;
	}

	@Nullable
	public String structurify$getStructureSetId() {
		return this.structurify$structureSetId;
	}

	public int structurify$getOriginalSpacing() {
		return this.spacing;
	}

	public int structurify$getOriginalSeparation() {
		return this.offset;
	}

	@ModifyReturnValue(
		method = "salt",
		at = @At("RETURN"),
		remap = true
	)
	private int structurify$modifySalt(int originalSalt) {
		return RandomSpreadUtil.getModifiedSalt(this.structurify$getStructureSetId(), originalSalt);
	}

	@ModifyReturnValue(
		method = "frequency",
		at = @At("RETURN"),
		remap = true
	)
	private float structurify$modifyFrequency(float originalFrequency) {
		return RandomSpreadUtil.getModifiedFrequency(this.structurify$getStructureSetId(), originalFrequency);
	}

	@ModifyReturnValue(
		method = "spacing",
		at = @At("RETURN")
	)
	protected int structurify$getSpacing(int originalSpacing) {
		return RandomSpreadUtil.getModifiedSpacing(this.structurify$getStructureSetId(), originalSpacing);
	}

	@ModifyReturnValue(
		method = "offset",
		at = @At("RETURN")
	)
	protected int structurify$getOffset(int originalOffset) {
		return RandomSpreadUtil.getModifiedSeparation(this.structurify$getStructureSetId(), this.spacing(), originalOffset);
	}
}
//?}
