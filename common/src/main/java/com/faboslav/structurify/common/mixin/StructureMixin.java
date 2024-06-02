package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Structure.class)
public abstract class StructureMixin implements StructurifyStructure
{
	@Nullable
	public Identifier structureIdentifier = null;

	public void structurify$setStructureIdentifier(Identifier structureSetIdentifier) {
		this.structureIdentifier = structureSetIdentifier;
	}

	@Nullable
	public Identifier structurify$getStructureIdentifier() {
		return this.structureIdentifier;
	}
}
