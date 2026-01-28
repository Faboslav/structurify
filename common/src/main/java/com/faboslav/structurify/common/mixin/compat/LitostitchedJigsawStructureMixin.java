package com.faboslav.structurify.common.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;

//? if lithostitched {
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawStructure;
import com.faboslav.structurify.common.api.StructurifyStructure;
import net.minecraft.world.level.levelgen.structure.Structure;

@Mixin(value = AlternateJigsawStructure.class)
public abstract class LitostitchedJigsawStructureMixin extends Structure implements StructurifyStructure
{
	protected LitostitchedJigsawStructureMixin(StructureSettings structureSettings) {
		super(structureSettings);
	}

}
//?} else {
/*import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

@Mixin(value = JigsawStructure.class)
public abstract class LitostitchedJigsawStructureMixin
{
}
*///?}
