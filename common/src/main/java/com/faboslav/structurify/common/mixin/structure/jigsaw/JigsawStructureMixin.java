package com.faboslav.structurify.common.mixin.structure.jigsaw;

import com.faboslav.structurify.common.mixin.structure.StructureMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMixin extends StructureMixin
{
	@Unique
	@Nullable
	//? if >= 1.21.9 {
	private JigsawStructure.MaxDistance structurify$maxDistance = null;
	//?} else {
	/*private int structurify$maxDistance = null;
	*///?}

	@Override
	public void structurify$setStructureIdentifier(Identifier structureSetIdentifier) {
		super.structurify$setStructureIdentifier(structureSetIdentifier);
		this.structurify$maxDistance = null;
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure;maxDepth:I",
			opcode = Opcodes.GETFIELD
		)
	)
	protected int structurify$findGenerationPointGetMaxDepth(int originalMaxDepth) {
		var structureData = this.structurify$getStructureData();

		if (structureData == null) {
			return originalMaxDepth;
		}

		return structureData.getJigsawData().getSize();
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			//? if >= 1.21.9 {
			target = "Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure;maxDistanceFromCenter:Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure$MaxDistance;",
			//?} else {
			/*target = "Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure;maxDistanceFromCenter:I",
			*///?}
			opcode = Opcodes.GETFIELD
		)
	)
	//? if >= 1.21.9 {
	protected JigsawStructure.MaxDistance structurify$findGenerationPointGetMaxDistanceFromCenter(JigsawStructure.MaxDistance originalMaxDistanceFromCenter)
	 //?} else {
	/*protected int structurify$findGenerationPointGetMaxDistanceFromCenter(int originalMaxDistanceFromCenter)
	*///?}
	{
		var structureData = this.structurify$getStructureData();

		if (structureData == null) {
			return originalMaxDistanceFromCenter;
		}

		if(this.structurify$maxDistance == null) {
			//? if >= 1.21.9 {
			this.structurify$maxDistance = new JigsawStructure.MaxDistance(
				structureData.getJigsawData().getHorizontalMaxDistanceFromCenter(),
				structureData.getJigsawData().getVerticalMaxDistanceFromCenter()
			);
			//?} else {
			/*this.structurify$maxDistance = structureData.getJigsawData().getHorizontalMaxDistanceFromCenter();
			 *///?}
		}

		return this.structurify$maxDistance;
	}
}
