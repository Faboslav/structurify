package com.faboslav.structurify.common.world.level.structure;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;

public record SpeculativeStructureBiomePredicate(Predicate<Holder<Biome>> validBiome) implements Predicate<Holder<Biome>>
{
	@Override
	public boolean test(Holder<Biome> biome) {
		return this.validBiome.test(biome);
	}
}
