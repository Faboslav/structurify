package com.faboslav.structurify.common.modcompat;

import net.minecraft.resources.Identifier;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked"})
public final class TerraCompat implements ModCompat
{
	@Override
	public EnumSet<Type> compatTypes() {
		return EnumSet.of(Type.BIOME_REPLACER);
	}

	@Override
	public List<String> getReplacedBiomes(List<String> biomes) {
		var terraBiomes = new ArrayList<String>();
		var terraBiomeMap = this.getTerraBiomes("com.dfsek.terra.mod.util.BiomeUtil", "getTerraBiomeMap");

		if (terraBiomeMap == null) {
			terraBiomeMap = this.getTerraBiomes("com.dfsek.terra.mod.util.MinecraftUtil", "getTerraBiomeMap");
		}

		if (terraBiomeMap != null) {
			terraBiomeMap.forEach((originalBiome, replacementBiomes) -> {
				var vanillaBiomeName = originalBiome.toString();

				if (biomes.contains(vanillaBiomeName)) {
					for (var replacementBiome : replacementBiomes) {
						terraBiomes.add(replacementBiome.toString());
					}
				}
			});
		}

		return terraBiomes;
	}

	public Map<Identifier, List<Identifier>> getTerraBiomes(String className, String methodName) {
		try {
			Class<?> clazz = Class.forName(className);
			Method method = clazz.getDeclaredMethod(methodName);
			method.setAccessible(true);
			Object result = method.invoke(null);
			return (Map<Identifier, List<Identifier>>) result;

		} catch (Exception e) {
			return null;
		}
	}
}