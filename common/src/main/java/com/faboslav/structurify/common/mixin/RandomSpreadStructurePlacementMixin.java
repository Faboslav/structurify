package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement.class)
public abstract class RandomSpreadStructurePlacementMixin extends StructurePlacement implements StructurifyRandomSpreadStructurePlacement
{
	@Shadow
	@Final
	private int spacing;

	@Shadow
	@Final
	private int separation;

	@Nullable
	public Identifier structureSetIdentifier = null;

	protected RandomSpreadStructurePlacementMixin(
		Vec3i locateOffset,
		FrequencyReductionMethod frequencyReductionMethod,
		float frequency,
		int salt,
		Optional<ExclusionZone> exclusionZone
	) {
		super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
	}

	public void structurify$setStructureSetIdentifier(Identifier structureSetIdentifier) {
		this.structureSetIdentifier = structureSetIdentifier;
	}

	@Nullable
	public Identifier structurify$getStructureIdentifier() {
		return this.structureSetIdentifier;
	}

	public int structurify$getOriginalSpacing() {
		return this.spacing;
	}

	public int structurify$getOriginalSeparation() {
		return this.separation;
	}

	@Inject(
		method = "getSpacing",
		at = @At("RETURN"),
		cancellable = true
	)
	protected void structurify$getSpacing(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(this.structurify$getModifiedSpacing(cir.getReturnValue()));
	}

	@Inject(
		method = "getSeparation",
		at = @At("RETURN"),
		cancellable = true
	)
	protected void structurify$getSeparation(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(this.structurify$getModifiedSeparation(cir.getReturnValue()));
	}

	@ModifyExpressionValue(
		method = {"getStartChunk"},
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/chunk/placement/RandomSpreadStructurePlacement;spacing:I", opcode = Opcodes.GETFIELD)
	)
	protected int structurify$getStartChunkGetSpacing(int originalSpacing) {
		return this.structurify$getModifiedSpacing(originalSpacing);
	}

	@ModifyExpressionValue(
		method = {"getStartChunk"},
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/chunk/placement/RandomSpreadStructurePlacement;separation:I", opcode = Opcodes.GETFIELD)
	)
	protected int structurify$getStartChunkGetSeparation(int originalSeparation) {
		return this.structurify$getModifiedSeparation(originalSeparation);
	}

	protected int structurify$getModifiedSpacing(int originalSpacing) {
		Identifier structureSetIdentifier = structurify$getStructureIdentifier();

		if (structureSetIdentifier == null) {
			return this.structurify$getCorrectedModifiedSpacingValue(originalSpacing);
		}

		if (!Structurify.getConfig().getStructureSetData().containsKey(structureSetIdentifier.toString())) {
			return this.structurify$getCorrectedModifiedSpacingValue(originalSpacing);
		}

		StructureSetData structureSetData = Structurify.getConfig().getStructureSetData().get(structureSetIdentifier.toString());

		if (structureSetData.isUsingDefaultSpacing()) {
			return this.structurify$getCorrectedModifiedSpacingValue(originalSpacing);
		}

		return structureSetData.getSpacing();
	}

	protected int structurify$getModifiedSeparation(int originalSeparation) {
		Identifier structureSetIdentifier = structurify$getStructureIdentifier();

		if (structureSetIdentifier == null) {
			return this.structurify$getCorrectedModifiedSeparationValue(originalSeparation);
		}

		if (!Structurify.getConfig().getStructureSetData().containsKey(structureSetIdentifier.toString())) {
			return this.structurify$getCorrectedModifiedSeparationValue(originalSeparation);
		}

		StructureSetData structureSetData = Structurify.getConfig().getStructureSetData().get(structureSetIdentifier.toString());

		if (structureSetData.isUsingDefaultSeparation()) {
			return this.structurify$getCorrectedModifiedSeparationValue(originalSeparation);
		}

		return structureSetData.getSeparation();
	}

	protected int structurify$getCorrectedModifiedSeparationValue(int separationValue) {
		return Math.max(0, (int) (((double) separationValue) * Structurify.getConfig().globalSpacingAndSeparationModifier));
	}

	protected int structurify$getCorrectedModifiedSpacingValue(int spacingValue) {
		return Math.max(1, (int) (((double) spacingValue) * Structurify.getConfig().globalSpacingAndSeparationModifier));
	}
}
