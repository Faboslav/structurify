package com.faboslav.structurify.common.mixin.plugin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class StructurifyMixinPlugin implements IMixinConfigPlugin
{
	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (mixinClassName.equals("com.faboslav.structurify.common.mixin.WorldOpenFlowsMixin")) {
			return this.isClassAvailable("me.earth.mc_runtime_test.McRuntimeTest");
		}

		if (mixinClassName.equals("com.faboslav.structurify.common.mixin.compat.RepurposedStructuresModifySpreadMixin")) {
			return this.isClassAvailable("com.telepathicgrunt.repurposedstructures.world.structures.placements.AdvancedRandomSpread");
		}

		if (mixinClassName.equals("com.faboslav.structurify.common.mixin.compat.YungJigsawStructureMixin")) {
			return this.isClassAvailable("com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure");
		}

		if (mixinClassName.equals("com.faboslav.structurify.forge.mixin.compat.StructureGelApiModifySpreadMixin")) {
			return this.isClassAvailable("com.legacy.structure_gel.api.structure.GridStructurePlacement");
		}

		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	private boolean isClassAvailable(String className) {
		String classPath = className.replace('.', '/') + ".class";
		return getClass().getClassLoader().getResource(classPath) != null;
	}
}