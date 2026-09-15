package com.faboslav.structurify.common.mixin.plugin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StructurifyMixinPlugin implements IMixinConfigPlugin
{
	private String mixinPackage;

	@Override
	public void onLoad(String mixinPackage) {
		this.mixinPackage = mixinPackage;
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

		if (mixinClassName.equals("com.faboslav.structurify.common.mixin.WorldOptionsMixin")) {
			return this.isClassAvailable("me.earth.mc_runtime_test.McRuntimeTest");
		}

		// YACL
		if (mixinClassName.equals("com.faboslav.structurify.common.mixin.yacl.ElementListWidgetExtMixin")) {
			return this.isClassAvailable("dev.isxander.yacl3.gui.ElementListWidgetExt");
		}

		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		List<String> mixins = new ArrayList<>();

		// Common
		if (this.mixinPackage.equals("com.faboslav.structurify.common.mixin")) {
			if (this.isClassAvailable("com.faboslav.structurify.common.mixin.compat.lithostitched.LithostitchedAlternateJigsawStructureMixin") && this.isClassAvailable("dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawStructure")) {
				mixins.add("compat.lithostitched.LithostitchedAlternateJigsawStructureMixin");
			}
			if (this.isClassAvailable("com.faboslav.structurify.common.mixin.compat.yungsapi.YungJigsawStructureMixin") && this.isClassAvailable("com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure")) {
				mixins.add("compat.yungsapi.YungJigsawStructureMixin");
			}

			if (this.isClassAvailable("com.faboslav.structurify.common.mixin.compat.repurposedstructures.RepurposedStructuresGenericJigsawStructureMixin") && this.isClassAvailable("com.telepathicgrunt.repurposedstructures.world.structures.GenericJigsawStructure")) {
				mixins.add("compat.repurposedstructures.RepurposedStructuresGenericJigsawStructureMixin");
			}

			if (this.isClassAvailable("com.faboslav.structurify.common.mixin.compat.repurposedstructures.RepurposedStructuresModifySpreadMixin") && this.isClassAvailable("com.telepathicgrunt.repurposedstructures.world.structures.placements.AdvancedRandomSpread")) {
				mixins.add("compat.repurposedstructures.RepurposedStructuresModifySpreadMixin");
			}
		}

		// NeoForge
		if (this.mixinPackage.equals("com.faboslav.structurify.neoforge.mixin")) {
			if(this.isClassAvailable("com.faboslav.structurify.neoforge.mixin.compat.cataclysm.CataclysmRandomSpreadMixin") && this.isClassAvailable("com.github.L_Ender.cataclysm.world.structures.placements.CataclysmRandomSpread")) {
				mixins.add("compat.cataclysm.CataclysmRandomSpreadMixin");
			}

			if(this.isClassAvailable("com.faboslav.structurify.neoforge.mixin.compat.cataclysm.CataclysmStructureMixin") && this.isClassAvailable("com.github.L_Ender.cataclysm.structures.CataclysmStructure")) {
				mixins.add("compat.cataclysm.CataclysmStructureMixin");
			}

			if(this.isClassAvailable("com.faboslav.structurify.neoforge.mixin.compat.cataclysm.CataclysmJigsawStructureMixin") && this.isClassAvailable("com.github.L_Ender.cataclysm.structures.jisaw.CataclysmJigsawStructure")) {
				mixins.add("compat.cataclysm.CataclysmJigsawStructureMixin");
			}
		}

		// Forge
		if (this.mixinPackage.equals("com.faboslav.structurify.forge.mixin")) {
			if (this.isClassAvailable("com.faboslav.structurify.forge.mixin.compat.structuregelapi.StructureGelApiGridStructurePlacement") && this.isClassAvailable("com.legacy.structure_gel.api.structure.GridStructurePlacement")) {
				mixins.add("compat.structuregelapi.StructureGelApiGridStructurePlacement");
			}
			if(this.isClassAvailable("com.faboslav.structurify.forge.mixin.compat.cataclysm.CataclysmRandomSpreadMixin") && this.isClassAvailable("com.github.L_Ender.cataclysm.world.structures.placements.CataclysmRandomSpread")) {
				mixins.add("compat.cataclysm.CataclysmRandomSpreadMixin");
			}

			if(this.isClassAvailable("com.faboslav.structurify.forge.mixin.compat.cataclysm.CataclysmStructureMixin") && this.isClassAvailable("com.github.L_Ender.cataclysm.structures.CataclysmStructure")) {
				mixins.add("compat.cataclysm.CataclysmStructureMixin");
			}

			if(this.isClassAvailable("com.faboslav.structurify.forge.mixin.compat.cataclysm.CataclysmJigsawStructureMixin") && this.isClassAvailable("com.github.L_Ender.cataclysm.structures.jisaw.CataclysmJigsawStructure")) {
				mixins.add("compat.cataclysm.CataclysmJigsawStructureMixin");
			}
		}

		return mixins;
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
