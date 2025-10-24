package com.faboslav.structurify.common.mixin.structure.jigsaw;

import com.faboslav.structurify.common.mixin.structure.StructureMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMixin extends StructureMixin
{
	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure;maxDepth:I",
			opcode = Opcodes.GETFIELD
		)
	)
	protected int structurify$findGenerationPointGetMaxDepth(int originalMaxDepth) {
		if (this.structurify$getStructureData() == null) {
			return originalMaxDepth;
		}

		return this.structurify$getStructureData().getJigsawData().getSize();
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			//? if >= 1.21.9 {
			/*target = "Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure;maxDistanceFromCenter:Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure$MaxDistance;",
			*///?} else {
			target = "Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure;maxDistanceFromCenter:I",
			//?}
			opcode = Opcodes.GETFIELD
		)
	)
		//? if >= 1.21.9 {
	/*protected JigsawStructure.MaxDistance structurify$findGenerationPointGetMaxDistanceFromCenter(JigsawStructure.MaxDistance originalMaxDistanceFromCenter)
	 *///?} else {
	protected int structurify$findGenerationPointGetMaxDistanceFromCenter(int originalMaxDistanceFromCenter)
	//?}
	{
		if (this.structurify$getStructureData() == null) {
			return originalMaxDistanceFromCenter;
		}

		//? if >= 1.21.9 {
		/*return new JigsawStructure.MaxDistance(
			this.structurify$getStructureData().getJigsawData().getHorizontalMaxDistanceFromCenter(),
			this.structurify$getStructureData().getJigsawData().getVerticalMaxDistanceFromCenter()
		);
		*///?} else {
		return this.structurify$getStructureData().getJigsawData().getHorizontalMaxDistanceFromCenter();
		//?}
	}
}
