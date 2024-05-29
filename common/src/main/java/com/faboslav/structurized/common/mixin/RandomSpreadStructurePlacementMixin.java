package com.faboslav.structurized.common.mixin;

import com.faboslav.structurized.common.Structurized;
import com.faboslav.structurized.common.api.StructurizedRandomSpreadStructurePlacement;
import com.faboslav.structurized.common.config.data.StructureSetData;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RandomSpreadStructurePlacement.class)
public abstract class RandomSpreadStructurePlacementMixin extends StructurePlacement implements StructurizedRandomSpreadStructurePlacement
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

	public void structurized$setStructureIdentifier(Identifier structureSetIdentifier) {
		this.structureSetIdentifier = structureSetIdentifier;
	}

	@Nullable
	public Identifier structurized$getStructureIdentifier() {
		return this.structureSetIdentifier;
	}

	@Inject(
		method = "getSpacing",
		at = @At("RETURN"),
		cancellable = true
	)
	private void structurized$getSpacing(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(this.structurized$getModifiedSpacing(cir.getReturnValue()));
	}

	private int structurized$getModifiedSpacing(int originalSpacing) {
		if(!Structurized.getConfig().isLoaded) {
			return originalSpacing;
		}

		Identifier structureSetIdentifier = structurized$getStructureIdentifier();

		if (structureSetIdentifier == null) {
			return (int)((double)originalSpacing * Structurized.getConfig().globalSpacingAndSeparationModifier);
		}

		if(!Structurized.getConfig().getStructureSetData().containsKey(structureSetIdentifier.toString())) {
			return (int)((double)originalSpacing * Structurized.getConfig().globalSpacingAndSeparationModifier);
		}

		StructureSetData structureSetData = Structurized.getConfig().getStructureSetData().get(structureSetIdentifier.toString());

		if(structureSetData.isUsingDefaultSpacing()) {
			return (int)((double)originalSpacing * Structurized.getConfig().globalSpacingAndSeparationModifier);
		}

		return structureSetData.getSpacing();
	}

	@Inject(
		method = "getSeparation",
		at = @At("RETURN"),
		cancellable = true
	)
	private void structurized$getSeparation(CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue(this.structurized$getModifiedSpacing(cir.getReturnValue()));
	}

	@ModifyExpressionValue(
		method = {"getStartChunk"},  // List all methods that access the fields
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/chunk/placement/RandomSpreadStructurePlacement;spacing:I", opcode = Opcodes.GETFIELD)
	)
	private int redirectGetSpacing(int originalSpacing) {
		return this.structurized$getModifiedSpacing(originalSpacing);
	}

	@ModifyExpressionValue(
		method = {"getStartChunk"},  // List all methods that access the fields
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/gen/chunk/placement/RandomSpreadStructurePlacement;separation:I", opcode = Opcodes.GETFIELD)
	)
	private int redirectGetSeparation(int original) {
		return original;
	}
}
