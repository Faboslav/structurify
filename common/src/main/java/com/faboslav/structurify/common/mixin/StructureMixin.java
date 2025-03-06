package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.api.StructurifyStructure;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Structure.class)
public abstract class StructureMixin implements StructurifyStructure
{
	@Unique
	@Nullable
	public ResourceLocation structurify$structureIdentifier = null;

	public void structurify$setStructureIdentifier(ResourceLocation structureSetIdentifier) {
		this.structurify$structureIdentifier = structureSetIdentifier;
	}

	@Nullable
	public ResourceLocation structurify$getStructureIdentifier() {
		return this.structurify$structureIdentifier;
	}

	@Unique
	@Nullable
	public HolderSet<Biome> structurify$structureBiomes = null;

	public void structurify$setStructureBiomes(@Nullable HolderSet<Biome> biomeHolderSet) {
		this.structurify$structureBiomes = biomeHolderSet;
	}

	@Nullable
	public HolderSet<Biome> structurify$getStructureBiomes() {
		return this.structurify$structureBiomes;
	}
}
