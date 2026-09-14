//? if cataclysm {
package com.faboslav.structurify.forge.mixin.compat.cataclysm;

import com.faboslav.structurify.common.mixin.structure.StructureMixin;
import com.github.L_Ender.cataclysm.structures.Burning_Arena_Structure;
import com.github.L_Ender.cataclysm.structures.CataclysmStructure;
import com.github.L_Ender.cataclysm.structures.Cursed_Pyramid_Structure;
import com.github.L_Ender.cataclysm.structures.RuinedCitadelStructure;
import com.github.L_Ender.cataclysm.structures.Sunken_City_Structure;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.levelgen.GenerationStep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(
	value = {
		CataclysmStructure.class,
		Burning_Arena_Structure.class,
		Cursed_Pyramid_Structure.class,
		RuinedCitadelStructure.class,
		Sunken_City_Structure.class
	}
)
public abstract class CataclysmStructureMixin extends StructureMixin
{
	@WrapMethod(
		method = "step",
		require = 0
	)
	private GenerationStep.Decoration structurify$getStep(
		Operation<GenerationStep.Decoration> original
	) {
		if (this.structurify$structureStep == null) {
			var structureData = this.structurify$getStructureData();

			if (structureData == null) {
				this.structurify$structureStep = original.call();
			} else {
				this.structurify$structureStep = structureData.getStep();
			}
		}

		return this.structurify$structureStep;
	}
}
//?}
