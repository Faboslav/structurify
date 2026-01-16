package com.faboslav.structurify.common.mixin.structure.placement;

import com.faboslav.structurify.common.api.StructurifyStructurePlacement;
import com.faboslav.structurify.common.util.RandomSpreadUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructurePlacement.class)
public abstract class StructurePlacementMixin implements StructurifyStructurePlacement
{
	@Shadow
	@Final
	private int salt;

	@Shadow
	@Final
	private float frequency;

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

	public int structurify$getOriginalSalt() {
		return this.salt;
	}

	public float structurify$getOriginalFrequency() {
		return this.frequency;
	}

	@ModifyReturnValue(
		method = "salt",
		at = @At("RETURN")
	)
	protected int structurify$getSalt(int originalSalt) {
		return RandomSpreadUtil.getModifiedSalt(this.structurify$getStructureSetId(), originalSalt);
	}

	@ModifyReturnValue(
		method = "frequency",
		at = @At("RETURN")
	)
	protected float structurify$getFrequency(float originalFrequency) {
		return RandomSpreadUtil.getModifiedFrequency(this.structurify$getStructureSetId(), originalFrequency);
	}

	//? if >= 1.21.1 {
	@ModifyExpressionValue(
		method = "applyAdditionalChunkRestrictions",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/level/levelgen/structure/placement/StructurePlacement;frequency:F",
			opcode = Opcodes.GETFIELD
		)
	)
	protected float structurify$applyAdditionalChunkRestrictionsGetFrequency(float originalFrequency) {
		return RandomSpreadUtil.getModifiedFrequency(this.structurify$getStructureSetId(), originalFrequency);
	}
	//?} else {
	/*@ModifyExpressionValue(
		method = "isStructureChunk",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/level/levelgen/structure/placement/StructurePlacement;frequency:F",
			opcode = Opcodes.GETFIELD
		)
	)
	protected float structurify$isStructureChunkGetFrequency(float originalFrequency) {
		return RandomSpreadUtil.getModifiedFrequency(this.structurify$getStructureSetId(), originalFrequency);
	}
	*///?}
}
