package com.faboslav.structurify.common.api;

import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public interface StructurifyStructure
{
	void structurify$setStructureIdentifier(@Nullable ResourceLocation structureSetIdentifier);

	@Nullable
	ResourceLocation structurify$getStructureIdentifier();

	void structurify$setStructureBiomes(@Nullable HolderSet<Biome> biomeHolderSet);

	@Nullable
	HolderSet<Biome> structurify$getStructureBiomes();
}
