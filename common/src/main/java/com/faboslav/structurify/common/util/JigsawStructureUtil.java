package com.faboslav.structurify.common.util;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.mixin.structure.jigsaw.JigsawStructureAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

//? if yungs_api {
/*import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
*///?}

//? if repurposed_structures {
import com.telepathicgrunt.repurposedstructures.world.structures.GenericJigsawStructure;
//?}

//? if yungs_api || repurposed_structures {
import com.faboslav.structurify.common.platform.PlatformHooks;
//?}

//? if >= 1.21.9 {
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
//?}

import java.lang.reflect.Field;
import java.util.Optional;

public final class JigsawStructureUtil
{
	public static boolean isJigsawLikeStructure(Structure structure) {
		if (structure instanceof JigsawStructure) {
			return true;
		}

		//? if yungs_api {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("yungsapi") && structure instanceof YungJigsawStructure) {
			return true;
		}
		*///?}

		//? if repurposed_structures {
		if (PlatformHooks.PLATFORM_HELPER.isModLoaded("repurposed_structures") && structure instanceof GenericJigsawStructure) {
			return true;
		}
		//?}

		Class<?> clazz = structure.getClass();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (field.getName().equals("maxDistanceFromCenter") || field.getName().equals("maxDepth") || field.getName().equals("size")) {
				return true;
			}
		}

		return false;
	}

	//? if >= 1.21.9 {
	public static JigsawStructure.MaxDistance getMaxDistanceFromCenterForStructure(Structure structure)
	 //?} else {
	/*public static int getMaxDistanceFromCenterForStructure(Structure structure)
	*///?}
	{
		if (structure instanceof JigsawStructure) {
			return ((JigsawStructureAccessor) structure).structurify$getMaxDistanceFromCenter();
		}

		//? if yungs_api {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("yungsapi") && structure instanceof YungJigsawStructure) {
			return ((YungJigsawStructure) structure).maxDistanceFromCenter;
		}
		*///?}

		//? if repurposed_structures {
		if (PlatformHooks.PLATFORM_HELPER.isModLoaded("repurposed_structures") && structure instanceof GenericJigsawStructure) {
			return ((GenericJigsawStructure) structure).maxDistanceFromCenter.orElse(0);
		}
		//?}

		Class<?> clazz = structure.getClass();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (field.getName().equals("maxDistanceFromCenter")) {
				field.setAccessible(true);

				//? if >= 1.21.9 {
				try {
					Field target = null;

					for (Field f : structure.getClass().getDeclaredFields()) {
						if (f.getName().equals("maxDistanceFromCenter")) { target = f; break; }
					}

					if (target == null) {
						return new JigsawStructure.MaxDistance(0, 0);
					}

					target.setAccessible(true);
					Object raw = target.get(structure);
					Object val = (raw instanceof Optional<?> opt) ? opt.orElse(null) : raw;

					if (val == null) {
						return new JigsawStructure.MaxDistance(0, 0);
					}

					if (val instanceof Integer i) {
						return new JigsawStructure.MaxDistance(i, i);
					}

					try {
						Method horizontalMethod = val.getClass().getMethod("horizontal");
						Method verticalMethod = val.getClass().getMethod("vertical");
						int horizontal = ((Number) horizontalMethod.invoke(val)).intValue();
						int vertical = ((Number) verticalMethod.invoke(val)).intValue();
						return new JigsawStructure.MaxDistance(horizontal, vertical);
					} catch (NoSuchMethodException ignore) {
						int horizontal = 0;
						int vertical = 0;

						if (val.getClass().isRecord()) {
							for (RecordComponent recordComponent : val.getClass().getRecordComponents()) {
								if (recordComponent.getName().equals("horizontal")) {
									horizontal = ((Number) recordComponent.getAccessor().invoke(val)).intValue();
								} else if (recordComponent.getName().equals("vertical")) {
									vertical = ((Number) recordComponent.getAccessor().invoke(val)).intValue();
								}
							}
							if (horizontal != 0 || vertical != 0) {
								return new JigsawStructure.MaxDistance(horizontal == 0 ? vertical : horizontal, vertical == 0 ? horizontal : vertical);
							}
						}

						try {
							Field horizontalField = val.getClass().getDeclaredField("horizontal");
							Field verticalField = val.getClass().getDeclaredField("vertical");
							horizontalField.setAccessible(true);
							verticalField.setAccessible(true);
							horizontal = ((Number) horizontalField.get(val)).intValue();
							vertical = ((Number) verticalField.get(val)).intValue();

							return new JigsawStructure.MaxDistance(horizontal, vertical);
						} catch (NoSuchFieldException e) {
							Structurify.getLogger().error(e.getMessage());
						}
					}
				} catch (Throwable e) {
					Structurify.getLogger().error(e.getMessage());
				}
				//?} else {
				/*try {
					if (Optional.class.isAssignableFrom(field.getType())) {
						Optional<?> optionalValue = (Optional<?>) field.get(structure);
						return optionalValue.map(val -> (Integer) val).orElse(0);
					}

					return field.getInt(structure);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					Structurify.getLogger().error(e.getMessage());
				}
				*///?}

				break;
			}
		}

		//? if >= 1.21.9 {
		return new JigsawStructure.MaxDistance(0, 0);
		 //?} else {
		/*return 0;
		*///?}
	}

	public static int getSizeForStructure(Structure structure) {
		if (structure instanceof JigsawStructure) {
			return ((JigsawStructureAccessor) structure).structurify$getMaxDepth();
		}

		//? if yungs_api {
		/*if (PlatformHooks.PLATFORM_HELPER.isModLoaded("yungsapi") && structure instanceof YungJigsawStructure) {
			return ((YungJigsawStructure) structure).maxDepth;
		}
		*///?}

		//? if repurposed_structures {
		if (PlatformHooks.PLATFORM_HELPER.isModLoaded("repurposed_structures") && structure instanceof GenericJigsawStructure) {
			return ((GenericJigsawStructure) structure).size;
		}
		//?}

		Class<?> clazz = structure.getClass();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			if (field.getName().equals("size") || field.getName().equals("max_depth")) {
				field.setAccessible(true);

				try {
					if (Optional.class.isAssignableFrom(field.getType())) {
						Optional<?> optionalValue = (Optional<?>) field.get(structure);
						return optionalValue.map(val -> (Integer) val).orElse(0);
					} else {
						return field.getInt(structure);
					}
				} catch (IllegalAccessException | IllegalArgumentException e) {
					Structurify.getLogger().error(e.getMessage());
				}

				break;
			}
		}

		return 0;
	}
}
