package com.faboslav.structurify.common.mixin.structure;

import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.util.BiomeUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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

	@Unique
	@Nullable
	public HolderSet<Biome> structurify$structureBlacklistedBiomes = null;

	public void structurify$setStructureBlacklistedBiomes(@Nullable HolderSet<Biome> biomeHolderSet) {
		this.structurify$structureBlacklistedBiomes = biomeHolderSet;
	}

	@Nullable
	public HolderSet<Biome> structurify$getStructureBlacklistedBiomes() {
		if (this.structurify$structureBlacklistedBiomes == null) {
			var blacklistedBiomeHolderSet = BiomeUtil.getBlacklistedBiomes(structurify$getStructureIdentifier());
			this.structurify$setStructureBlacklistedBiomes(blacklistedBiomeHolderSet);
		}

		return this.structurify$structureBlacklistedBiomes;
	}

	@WrapMethod(
		method = "biomes"
	)
	private HolderSet<Biome> structurify$biomes(
		Operation<HolderSet<Biome>> original
	) {
		if (this.structurify$structureBiomes == null) {
			var biomeHolderSet = BiomeUtil.getBiomes(structurify$getStructureIdentifier(), original.call());
			this.structurify$setStructureBiomes(biomeHolderSet);
		}

		return this.structurify$getStructureBiomes();
	}
}
