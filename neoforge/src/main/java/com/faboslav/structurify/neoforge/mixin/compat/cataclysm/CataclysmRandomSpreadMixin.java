//? if cataclysm {
/*package com.faboslav.structurify.neoforge.mixin.compat.cataclysm;

import com.faboslav.structurify.common.api.StructurifyRandomSpreadStructurePlacement;
import com.faboslav.structurify.common.util.RandomSpreadUtil;
import com.faboslav.structurify.common.world.level.structure.StructurePlacementResolver;
import com.github.L_Ender.cataclysm.world.structures.placements.CataclysmRandomSpread;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

@Pseudo
@Mixin(value = CataclysmRandomSpread.class, remap = false)
public abstract class CataclysmRandomSpreadMixin extends RandomSpreadStructurePlacement implements StructurifyRandomSpreadStructurePlacement
{
	public CataclysmRandomSpreadMixin(int spacing, int separation, RandomSpreadType spreadType, int salt
	) {
		super(spacing, separation, spreadType, salt);
	}

	@Shadow
	@Final
	public abstract int spacing();

	@Shadow
	public abstract Optional<Integer> minDistanceFromWorldOrigin();

	@ModifyReturnValue(
		method = "spacing",
		at = @At("RETURN"),
		require = 0
	)
	protected int structurify$getSpacing(int originalSpacing) {
		return RandomSpreadUtil.getModifiedSpacing(this.structurify$getStructureSetId(), originalSpacing);
	}

	@ModifyReturnValue(
		method = "separation",
		at = @At("RETURN"),
		require = 0
	)
	protected int structurify$getSeparation(int originalSeparation) {
		return RandomSpreadUtil.getModifiedSeparation(this.structurify$getStructureSetId(), this.spacing(), originalSeparation);
	}

	@ModifyExpressionValue(
		method = "getPotentialStructureChunk",
		at = @At(
			value = "FIELD",
			target = "Lcom/github/L_Ender/cataclysm/world/structures/placements/CataclysmRandomSpread;spacing:I",
			opcode = Opcodes.GETFIELD
		),
		require = 0
	)
	protected int structurify$getStartChunkGetSpacing(int originalSpacing) {
		return RandomSpreadUtil.getModifiedSpacing(this.structurify$getStructureSetId(), originalSpacing);
	}

	@ModifyExpressionValue(
		method = "getPotentialStructureChunk",
		at = @At(
			value = "FIELD",
			target = "Lcom/github/L_Ender/cataclysm/world/structures/placements/CataclysmRandomSpread;separation:I",
			opcode = Opcodes.GETFIELD
		),
		require = 0
	)
	protected int structurify$getStartChunkGetSeparation(int originalSeparation) {
		return RandomSpreadUtil.getModifiedSeparation(this.structurify$getStructureSetId(), this.spacing(), originalSeparation);
	}

	@WrapMethod(
		method = "isPlacementChunk",
		require = 0
	)
	protected boolean structurify$isPlacementChunk(
		ChunkGeneratorStructureState chunkGeneratorStructureState,
		int chunkX,
		int chunkZ,
		Operation<Boolean> original
	) {
		if (original.call(chunkGeneratorStructureState, chunkX, chunkZ)) {
			return true;
		}

		if (StructurePlacementResolver.shouldUseOriginalPlacementChunksOnly()) {
			return false;
		}

		int placementAttempts = RandomSpreadUtil.getPlacementAttempts(this.structurify$getStructureSetId());

		if (placementAttempts <= 1) {
			return false;
		}

		if (this.structurify$isWithinMinDistanceFromWorldOrigin(chunkX, chunkZ)) {
			return false;
		}

		return RandomSpreadUtil.isWithinPlacementSpread((RandomSpreadStructurePlacement) (Object) this, chunkX, chunkZ);
	}

	@Unique
	private boolean structurify$isWithinMinDistanceFromWorldOrigin(int chunkX, int chunkZ) {
		Optional<Integer> minDistanceFromWorldOrigin = this.minDistanceFromWorldOrigin();

		if (minDistanceFromWorldOrigin.isEmpty()) {
			return false;
		}

		long x = chunkX * 16L;
		long z = chunkZ * 16L;
		long minDistance = minDistanceFromWorldOrigin.get();

		return x * x + z * z < minDistance * minDistance;
	}
}
*///?}
