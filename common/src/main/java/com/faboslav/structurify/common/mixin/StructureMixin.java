package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.api.StructurifyStructure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Structure.class)
public abstract class StructureMixin implements StructurifyStructure
{
	@Nullable
	public ResourceLocation structureIdentifier = null;

	public void structurify$setStructureIdentifier(ResourceLocation structureSetIdentifier) {
		this.structureIdentifier = structureSetIdentifier;
	}

	@Nullable
	public ResourceLocation structurify$getStructureIdentifier() {
		return this.structureIdentifier;
	}
}
