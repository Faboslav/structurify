//? if lithostitched {
package com.faboslav.structurify.common.mixin.compat.lithostitched;

import com.faboslav.structurify.common.mixin.structure.StructureMixin;
import com.faboslav.structurify.common.modcompat.LithostitchedCompat;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawConfig;
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawStructure;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(value = AlternateJigsawStructure.class)
public abstract class LithostitchedAlternateJigsawStructureMixin extends StructureMixin
{
	@Unique
	@Nullable
	private AlternateJigsawConfig structurify$originalConfig = null;

	@Unique
	@Nullable
	private AlternateJigsawConfig structurify$modifiedConfig = null;

	@Override
	public void structurify$setStructureIdentifier(Identifier structureIdentifier) {
		super.structurify$setStructureIdentifier(structureIdentifier);
		this.structurify$originalConfig = null;
		this.structurify$modifiedConfig = null;
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Ldev/worldgen/lithostitched/worldgen/structure/AlternateJigsawStructure;config:Ldev/worldgen/lithostitched/worldgen/structure/AlternateJigsawConfig;",
			opcode = Opcodes.GETFIELD,
			remap = false
		)
	)
	private AlternateJigsawConfig structurify$findGenerationPointGetConfig(AlternateJigsawConfig originalConfig) {
		var structureData = this.structurify$getStructureData();

		if (structureData == null || structureData.getJigsawData().isUsingDefaultValues()) {
			return originalConfig;
		}

		if (this.structurify$modifiedConfig == null || this.structurify$originalConfig != originalConfig) {
			this.structurify$originalConfig = originalConfig;
			this.structurify$modifiedConfig = LithostitchedCompat.getModifiedConfig(originalConfig, structureData.getJigsawData());
		}

		return this.structurify$modifiedConfig == null ? originalConfig : this.structurify$modifiedConfig;
	}
}
//?}
