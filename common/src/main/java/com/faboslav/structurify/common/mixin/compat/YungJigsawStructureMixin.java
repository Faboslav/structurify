package com.faboslav.structurify.common.mixin.compat;

import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;

//? yungs_api {

/*import com.faboslav.structurify.common.api.StructurifyStructure;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = YungJigsawStructure.class, remap = false)
public abstract class YungJigsawStructureMixin extends Structure implements StructurifyStructure
{
	protected YungJigsawStructureMixin(StructureSettings settings) {
		super(settings);
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Lcom/yungnickyoung/minecraft/yungsapi/world/structure/YungJigsawStructure;maxDepth:I",
			opcode = Opcodes.GETFIELD
		)
	)
	protected int structurify$findGenerationPointGetMaxDepth(int originalMaxDepth) {
		if(this.structurify$getStructureData() == null) {
			return originalMaxDepth;
		}

		return this.structurify$getStructureData().getJigsawData().getSize();
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Lcom/yungnickyoung/minecraft/yungsapi/world/structure/YungJigsawStructure;maxDistanceFromCenter:I",
			opcode = Opcodes.GETFIELD
		)
	)
	protected int structurify$findGenerationPointGetMaxDistanceFromCenter(int originalMaxDistanceFromCenter) {
		if(this.structurify$getStructureData() == null) {
			return originalMaxDistanceFromCenter;
		}

		// TODO handle chance in 1.21.9
		return this.structurify$getStructureData().getJigsawData().getHorizontalMaxDistanceFromCenter();
	}
}
*///?} else {
@Mixin(value = JigsawStructure.class)
public abstract class YungJigsawStructureMixin
{
}
//?}
