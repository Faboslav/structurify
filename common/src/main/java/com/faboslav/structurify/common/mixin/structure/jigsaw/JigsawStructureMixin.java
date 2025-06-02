package com.faboslav.structurify.common.mixin.structure.jigsaw;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.checks.JigsawStructureBiomeCheck;
import com.faboslav.structurify.common.checks.JigsawStructureFlatnessCheck;
import com.faboslav.structurify.common.mixin.structure.StructureMixin;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationContext;
import net.minecraft.world.level.levelgen.structure.Structure.GenerationStub;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMixin extends StructureMixin
{
	@Shadow
	@Final
	private HeightProvider startHeight;

	@WrapMethod(
		method = "findGenerationPoint"
	)
	private Optional<GenerationStub> structurify$getStructurePosition(
		GenerationContext generationContext,
		Operation<Optional<GenerationStub>> original
	) {
		ResourceLocation structureId = structurify$getStructureIdentifier();

		if (
			structureId == null
			|| !Structurify.getConfig().getStructureData().containsKey(structureId.toString())
		) {
			return original.call(generationContext);
		}

		var structureData = Structurify.getConfig().getStructureData().get(structureId.toString());

		if (structureData.isFlatnessCheckEnabled()) {
			var flatnessCheckResult = JigsawStructureFlatnessCheck.checkFlatness(structureData, this.startHeight, generationContext);

			if (!flatnessCheckResult) {
				return Optional.empty();
			}
		}

		if (structureData.isBiomeCheckEnabled()) {
			var biomeCheckResult = JigsawStructureBiomeCheck.checkBiomes(structureData, this.startHeight, generationContext);

			if (!biomeCheckResult) {
				return Optional.empty();
			}
		}

		return original.call(generationContext);
	}
}
