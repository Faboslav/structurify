package com.faboslav.structurify.common.api;

import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public interface StructurifyStructure
{
	void structurify$setStructureIdentifier(@Nullable ResourceLocation structureSetIdentifier);

	@Nullable
	StructureNamespaceData structurify$getGlobalStructureNamespaceData();

	@Nullable
	StructureNamespaceData structurify$getStructureNamespaceData();

	@Nullable
	StructureNamespaceData structurify$getStructureNamespaceData(@Nullable ResourceLocation structureIdentifier);

	@Nullable
	StructureData structurify$getStructureData();

	@Nullable
	StructureData structurify$getStructureData(@Nullable ResourceLocation structureIdentifier);

	@Nullable
	ResourceLocation structurify$getStructureIdentifier();

	void structurify$setStructureBiomes(@Nullable HolderSet<Biome> biomeHolderSet);

	@Nullable
	HolderSet<Biome> structurify$getStructureBiomes();

	void structurify$setStructureBlacklistedBiomes(@Nullable HolderSet<Biome> biomeHolderSet);

	@Nullable
	HolderSet<Biome> structurify$getStructureBlacklistedBiomes();
}
