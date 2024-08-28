package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.util.RandomSpreadUtil;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(RandomSpreadStructurePlacement.class)
public abstract class RandomSpreadStructurePlacementMixin extends StructurePlacement implements StructurifyRandomSpreadStructurePlacement
{
	@Shadow
	@Final
	private int spacing;

	@Shadow
	@Final
	private int separation;

	@Shadow
	public abstract int getSeparation();

	@Shadow
	public abstract int getSpacing();

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
	public Identifier structurify$getStructureSetIdentifier() {
		return this.structureSetIdentifier;
	}

	public int structurify$getOriginalSpacing() {
		return this.spacing;
	}

	public int structurify$getOriginalSeparation() {
		return this.separation;
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
			target = "Lnet/minecraft/world/gen/chunk/placement/RandomSpreadStructurePlacement;spacing:I",
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
			target = "Lnet/minecraft/world/gen/chunk/placement/RandomSpreadStructurePlacement;separation:I",
			opcode = Opcodes.GETFIELD
		)
	)
	protected int structurify$getStartChunkGetSeparation(int originalSeparation) {
		return RandomSpreadUtil.getModifiedSeparation(this.structurify$getStructureSetIdentifier(), this.getSpacing(), originalSeparation);
	}
}
