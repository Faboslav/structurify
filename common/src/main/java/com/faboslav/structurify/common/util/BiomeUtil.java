package com.faboslav.structurify.common.util;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.modcompat.ModChecker;
import com.faboslav.structurify.common.modcompat.ModCompat;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.*;
import java.util.stream.Collectors;

public final class BiomeUtil
{
	//? if >= 1.21.1 {
	public static TagKey<Biome> C_IS_OCEAN = TagKey.create(Registries.BIOME, Structurify.makeNamespacedId("c:is_ocean"));
	public static TagKey<Biome> C_IS_RIVER = TagKey.create(Registries.BIOME, Structurify.makeNamespacedId("c:is_river"));
	//?} else {
	/*public static TagKey<Biome> C_IS_OCEAN = TagKey.create(Registries.BIOME, Structurify.makeNamespacedId("c:ocean"));
	public static TagKey<Biome> C_IS_RIVER = TagKey.create(Registries.BIOME, Structurify.makeNamespacedId("c:river"));
	*///?}

	public static Set<ResourceKey<Biome>> getOceanBiomes() {
		var biomeRegistry = StructurifyRegistryManagerProvider.getBiomeRegistry();

		if (biomeRegistry == null) {
			return new HashSet<>();
		}

		Set<ResourceKey<Biome>> oceanBiomes = new HashSet<>();

		biomeRegistry.get(BiomeTags.IS_OCEAN).ifPresent(named ->
			oceanBiomes.addAll(named.stream()
				.map(h -> h.unwrapKey().orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet()))
		);

		biomeRegistry.get(BiomeTags.IS_RIVER).ifPresent(named ->
			oceanBiomes.addAll(named.stream()
				.map(h -> h.unwrapKey().orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet()))
		);

		biomeRegistry.get(C_IS_OCEAN).ifPresent(named ->
			oceanBiomes.addAll(named.stream()
				.map(h -> h.unwrapKey().orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet()))
		);

		biomeRegistry.get(C_IS_RIVER).ifPresent(named ->
			oceanBiomes.addAll(named.stream()
				.map(h -> h.unwrapKey().orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet()))
		);

		return oceanBiomes;
	}

	public static HolderSet<Biome> getBiomes(
		ResourceLocation structureId,
		HolderSet<Biome> originalBiomes
	) {
		if (
			structureId == null
			|| !Structurify.getConfig().getStructureData().containsKey(structureId.toString())
		) {
			return originalBiomes;
		}

		var biomeRegistry = StructurifyRegistryManagerProvider.getBiomeRegistry();

		if (biomeRegistry == null) {
			return originalBiomes;
		}

		var structureData = Structurify.getConfig().getStructureData().get(structureId.toString());
		var biomeIds = structureData.getBiomes();

		return getBiomeHolders(biomeIds, biomeRegistry);
	}

	public static HolderSet<Biome> getBlacklistedBiomes(ResourceLocation structureId) {
		if (
			structureId == null
			|| !Structurify.getConfig().getStructureData().containsKey(structureId.toString())
		) {
			return HolderSet.direct(new ArrayList<>());
		}

		var biomeRegistry = StructurifyRegistryManagerProvider.getBiomeRegistry();

		if (biomeRegistry == null) {
			return HolderSet.direct(new ArrayList<>());
		}

		var structureData = Structurify.getConfig().getStructureData().get(structureId.toString());
		var blacklistedBiomeIds = structureData.getBiomeCheckData().getBlacklistedBiomes();

		return getBiomeHolders(blacklistedBiomeIds, biomeRegistry);
	}

	public static HolderSet<Biome> getBiomeHolders(
		List<String> biomeIds,
		HolderLookup.RegistryLookup<Biome> biomeRegistry
	) {
		for (ModCompat modCompat : ModChecker.BIOME_REPLACER_COMPATS) {
			try {
				var replacedBiomeIds = modCompat.getReplacedBiomes(biomeIds);
				biomeIds.addAll(replacedBiomeIds);
			} catch (Throwable e) {
				Structurify.getLogger().error("Failed to get replaced biomes from mod compat");
				e.printStackTrace();
			}
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
