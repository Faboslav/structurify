package com.faboslav.structurify.common.mixin.compat;

import com.faboslav.structurify.common.mixin.RandomSpreadStructurePlacementMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.telepathicgrunt.repurposedstructures.world.structures.placements.AdvancedRandomSpread;
import net.minecraft.util.math.Vec3i;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AdvancedRandomSpread.class)
public abstract class AdvancedRandomSpreadMixin extends RandomSpreadStructurePlacementMixin
{
	protected AdvancedRandomSpreadMixin(
		Vec3i locateOffset,
		FrequencyReductionMethod frequencyReductionMethod,
		float frequency,
		int salt,
		Optional<ExclusionZone> exclusionZone
	) {
		super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
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
		at = @At(value = "FIELD", target = "Lcom/telepathicgrunt/repurposedstructures/world/structures/placements/AdvancedRandomSpread;spacing:I", opcode = Opcodes.GETFIELD)
	)
	protected int structurify$getStartChunkGetSpacing(int originalSpacing) {
		return this.structurify$getModifiedSpacing(originalSpacing);
	}

	@ModifyExpressionValue(
		method = {"getStartChunk"},
		at = @At(value = "FIELD", target = "Lcom/telepathicgrunt/repurposedstructures/world/structures/placements/AdvancedRandomSpread;separation:I", opcode = Opcodes.GETFIELD)
	)
	protected int structurify$getStartChunkGetSeparation(int originalSeparation) {
		return this.structurify$getModifiedSeparation(originalSeparation);
	}
}
