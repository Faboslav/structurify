package com.faboslav.structurify.common.mixin.structure;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import java.util.ArrayList;

@Mixin(Structure.class)
public abstract class StructureModifyBiomesMixin implements StructurifyStructure
{
	@WrapMethod(
		method = "biomes"
	)
	private HolderSet<Biome> structurify$getModifiedBiomes(
		Operation<HolderSet<Biome>> original
	) {
		ResourceLocation structureId = structurify$getStructureIdentifier();

		if (
			structureId == null
			|| !Structurify.getConfig().getStructureData().containsKey(structureId.toString())
		) {
			return original.call();
		}

		var registryManager = StructurifyRegistryManagerProvider.getRegistryManager();

		if (registryManager == null) {
			return original.call();
		}

		Registry<Biome> biomeRegistry = registryManager.registry(Registries.BIOME).orElse(null);

		if(biomeRegistry == null) {
			return original.call();
		}

		var structureData = Structurify.getConfig().getStructureData().get(structureId.toString());
		var biomes = structureData.getBiomes();
		ArrayList<Holder<Biome>> biomeHolders = new ArrayList<>();

		for(var biomeId : biomes) {
			var biomeKey = ResourceKey.create(Registries.BIOME, Structurify.makeVanillaId(biomeId));
			var biomeHolder = biomeRegistry.getHolder(biomeKey).orElse(null);

			if (biomeHolder == null) {
				continue;
			}

			biomeHolders.add(biomeHolder);
		}

		return HolderSet.direct(biomeHolders);
	}
}