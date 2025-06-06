package com.faboslav.structurify.common.mixin.structure;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.modcompat.ModChecker;
import com.faboslav.structurify.common.modcompat.ModCompat;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;

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

	@WrapMethod(
		method = "biomes"
	)
	private HolderSet<Biome> structurify$biomes(
		Operation<HolderSet<Biome>> original
	) {
		if (this.structurify$getStructureBiomes() == null) {
			var biomeHolderSet = this.structurify$getBiomeHolderSet(original);
			this.structurify$setStructureBiomes(biomeHolderSet);
		}

		return this.structurify$getStructureBiomes();
	}

	@Unique
	private HolderSet<Biome> structurify$getBiomeHolderSet(
		Operation<HolderSet<Biome>> original
	) {
		ResourceLocation structureId = structurify$getStructureIdentifier();

		if (
			structureId == null
			|| !Structurify.getConfig().getStructureData().containsKey(structureId.toString())
		) {
			return original.call();
		}

		var biomeRegistry = StructurifyRegistryManagerProvider.getBiomeRegistry();

		if (biomeRegistry == null) {
			return original.call();
		}

		var structureData = Structurify.getConfig().getStructureData().get(structureId.toString());
		var biomeIds = structureData.getBiomes();

		for (ModCompat modCompat : ModChecker.BIOME_REPLACER_COMPATS) {
			biomeIds = modCompat.getReplacedBiomes(biomeIds);
		}

		ArrayList<Holder<Biome>> biomeHolders = new ArrayList<>();

		for (var biomeId : biomeIds) {
			if (biomeId.contains("#")) {
				var biomeTagKey = TagKey.create(Registries.BIOME, Structurify.makeNamespacedId(biomeId.replace("#", "")));
				var biomeTagHolder = biomeRegistry.get(biomeTagKey).orElse(null);

				if (biomeTagHolder == null) {
					continue;
				}

				for (var biomeHolder : biomeTagHolder.stream().toList()) {
					biomeHolders.add(biomeHolder);
				}
			} else {
				var biomeResourceKey = ResourceKey.create(Registries.BIOME, Structurify.makeNamespacedId(biomeId.replace("#", "")));
				var biomeHolder = biomeRegistry.get(biomeResourceKey).orElse(null);

				if (biomeHolder == null) {
					continue;
				}

				biomeHolders.add(biomeHolder);
			}
		}

		return HolderSet.direct(biomeHolders);
	}
}
