package com.faboslav.structurify.common.mixin.plugin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.util.Platform;
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
		if (mixinClassName.equals("com.faboslav.structurify.common.mixin.compat.RepurposedStructuresModifySpreadMixin")) {
			return this.isClassAvailable("com.telepathicgrunt.repurposedstructures.world.structures.placements.AdvancedRandomSpread");
		}

		if (mixinClassName.equals("com.faboslav.structurify.common.mixin.AnimatedDynamicTextureImageAccessor")) {
			return isClassAvailable("dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage");
		}

		Structurify.getLogger().info(mixinClassName);

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