package com.faboslav.structurify.neoforge.mixin;

import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.util.RandomSpreadUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.telepathicgrunt.repurposedstructures.world.structures.placements.AdvancedRandomSpread;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(AdvancedRandomSpread.class)
public abstract class RepurposedStructuresAdvancedRandomSpreadMixin extends RandomSpreadStructurePlacement implements StructurifyRandomSpreadStructurePlacement
{
	@Shadow
	public abstract int getSpacing();

	public RepurposedStructuresAdvancedRandomSpreadMixin(
		Vec3i locateOffset,
		FrequencyReductionMethod frequencyReductionMethod,
		float frequency,
		int salt,
		Optional<ExclusionZone> exclusionZone,
		int spacing,
		int separation,
		SpreadType spreadType
	) {
		super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone, spacing, separation, spreadType);
	}

	@ModifyReturnValue(
		method = "getSpacing",
		at = @At("RETURN")
	)
	protected int structurify$getSpacing(int originalSpacing) {
		return RandomSpreadUtil.getModifiedSpacing(this.structurify$getStructureSetIdentifier(), originalSpacing);
	}

	@ModifyReturnValue(
		method = "getSeparation",
		at = @At("RETURN")
	)
	protected int structurify$getSeparation(int originalSeparation) {
		return RandomSpreadUtil.getModifiedSeparation(this.structurify$getStructureSetIdentifier(), this.getSpacing(), originalSeparation);
	}

	@ModifyExpressionValue(
		method = "getStartChunk",
		at = @At(
			value = "FIELD",
			target = "Lcom/telepathicgrunt/repurposedstructures/world/structures/placements/AdvancedRandomSpread;spacing:I",
			opcode = Opcodes.GETFIELD
		)
	)
	protected int structurify$getStartChunkGetSpacing(int originalSpacing) {
		return RandomSpreadUtil.getModifiedSpacing(this.structurify$getStructureSetIdentifier(), originalSpacing);
	}

	@ModifyExpressionValue(
		method = "getStartChunk",
		at = @At(
			value = "FIELD",
			target = "Lcom/telepathicgrunt/repurposedstructures/world/structures/placements/AdvancedRandomSpread;separation:I",
			opcode = Opcodes.GETFIELD
		)
	)
	protected int structurify$getStartChunkGetSeparation(int originalSeparation) {
		return RandomSpreadUtil.getModifiedSeparation(this.structurify$getStructureSetIdentifier(), this.getSpacing(), originalSeparation);
	}
}
