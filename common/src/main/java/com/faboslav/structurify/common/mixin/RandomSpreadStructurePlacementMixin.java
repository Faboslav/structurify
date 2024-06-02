package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.RandomSpreadStructurePlacement;
import com.faboslav.structurify.common.config.data.StructureSetData;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement.class)
public abstract class RandomSpreadStructurePlacementMixin extends StructurePlacement implements RandomSpreadStructurePlacement
{
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

	@Inject(
		method = "getSpacing",
		at = @At("RETURN"),
		cancellable = true
	)
	private void structurify$getSpacing(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(this.structurify$getModifiedSpacing(cir.getReturnValue()));
	}

	@Inject(
		method = "getSeparation",
		at = @At("RETURN"),
		cancellable = true
	)
	private void structurify$getSeparation(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(this.structurify$getModifiedSeparation(cir.getReturnValue()));
	}

	@ModifyExpressionValue(
		method = {"getStartChunk"},
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/chunk/placement/RandomSpreadStructurePlacement;spacing:I", opcode = Opcodes.GETFIELD)
	)
	private int structurify$getStartChunkGetSpacing1(int originalSpacing) {
		return this.structurify$getModifiedSpacing(originalSpacing);
	}

	@ModifyExpressionValue(
		method = {"getStartChunk"},
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/chunk/placement/RandomSpreadStructurePlacement;separation:I", opcode = Opcodes.GETFIELD)
	)
	private int redirectGetSeparation(int originalSeparation) {
		return this.structurify$getModifiedSeparation(originalSeparation);
	}

	private int structurify$getModifiedSpacing(int originalSpacing) {
		Identifier structureSetIdentifier = structurify$getStructureIdentifier();

		if (structureSetIdentifier == null) {
			return (int) ((double) originalSpacing * Structurify.getConfig().globalSpacingAndSeparationModifier);
		}

		if (!Structurify.getConfig().getStructureSetData().containsKey(structureSetIdentifier.toString())) {
			return (int) ((double) originalSpacing * Structurify.getConfig().globalSpacingAndSeparationModifier);
		}

		StructureSetData structureSetData = Structurify.getConfig().getStructureSetData().get(structureSetIdentifier.toString());

		if (structureSetData.isUsingDefaultSpacing()) {
			return (int) ((double) originalSpacing * Structurify.getConfig().globalSpacingAndSeparationModifier);
		}

		return structureSetData.getSpacing();
	}

	private int structurify$getModifiedSeparation(int originalSeparation) {

		// TODO unite

		Identifier structureSetId = structurify$getStructureIdentifier();
		double globalModifier = Structurify.getConfig().globalSpacingAndSeparationModifier;

		if (structureSetId == null || !Structurify.getConfig().getStructureSetData().containsKey(structureSetId.toString())) {
			return (int) (originalSeparation * globalModifier);
		}

		StructureSetData structureSetData = Structurify.getConfig().getStructureSetData().get(structureSetId.toString());

		if (structureSetData.isUsingDefaultSpacing()) {
			return (int) (originalSeparation * globalModifier);
		}

		return structureSetData.getSeparation();
	}
}
