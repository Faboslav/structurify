package com.faboslav.structurify.common.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

//? if repurposed_structures {
import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.util.RandomSpreadUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.telepathicgrunt.repurposedstructures.world.structures.placements.AdvancedRandomSpread;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(value = AdvancedRandomSpread.class)
public abstract class RepurposedStructuresModifySpreadMixin extends RandomSpreadStructurePlacement implements StructurifyRandomSpreadStructurePlacement
{
	public RepurposedStructuresModifySpreadMixin(
		Vec3i vec3i,
		FrequencyReductionMethod frequencyReductionMethod,
		float f,
		int i,
		Optional<ExclusionZone> optional,
		int j,
		int k,
		RandomSpreadType randomSpreadType
	) {
		super(vec3i, frequencyReductionMethod, f, i, optional, j, k, randomSpreadType);
	}

	@Shadow
	@Final
	public abstract int spacing();

	@ModifyReturnValue(
		method = "spacing",
		at = @At("RETURN")
	)
	protected int structurify$getSpacing(int originalSpacing) {
		return RandomSpreadUtil.getModifiedSpacing(this.structurify$getStructureSetIdentifier(), originalSpacing);
	}

	@ModifyReturnValue(
		method = "separation",
		at = @At("RETURN")
	)
	protected int structurify$getSeparation(int originalSeparation) {
		return RandomSpreadUtil.getModifiedSeparation(this.structurify$getStructureSetIdentifier(), this.spacing(), originalSeparation);
	}

	@ModifyExpressionValue(
		method = "getPotentialStructureChunk",
		at = @At(
			value = "FIELD",
			target = "Lcom/telepathicgrunt/repurposedstructures/world/structures/placements/AdvancedRandomSpread;spacing:I",
			opcode = Opcodes.GETFIELD,
			remap = false
		)
	)
	protected int structurify$getStartChunkGetSpacing(int originalSpacing) {
		return RandomSpreadUtil.getModifiedSpacing(this.structurify$getStructureSetIdentifier(), originalSpacing);
	}

	@ModifyExpressionValue(
		method = "getPotentialStructureChunk",
		at = @At(
			value = "FIELD",
			target = "Lcom/telepathicgrunt/repurposedstructures/world/structures/placements/AdvancedRandomSpread;separation:I",
			opcode = Opcodes.GETFIELD,
			remap = false
		)
	)
	protected int structurify$getStartChunkGetSeparation(int originalSeparation) {
		return RandomSpreadUtil.getModifiedSeparation(this.structurify$getStructureSetIdentifier(), this.spacing(), originalSeparation);
	}
}
//?} else {
/*// This is just a placeholder mixin
@Mixin(RandomSpreadStructurePlacement.class)
public abstract class RepurposedStructuresModifySpreadMixin
{
}
*///?}