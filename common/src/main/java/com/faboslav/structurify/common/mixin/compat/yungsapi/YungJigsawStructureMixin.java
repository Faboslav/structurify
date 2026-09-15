//? if yungs_api {
/*package com.faboslav.structurify.common.mixin.compat.yungsapi;

import com.faboslav.structurify.common.api.StructurifyJigsawStructure;
import com.faboslav.structurify.common.mixin.structure.StructureMixin;
import com.yungnickyoung.minecraft.yungsapi.world.structure.YungJigsawStructure;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import org.objectweb.asm.Opcodes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

//? if >= 1.21.9 {
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
//?}

import java.util.Optional;

@Pseudo
@Mixin(value = YungJigsawStructure.class)
public abstract class YungJigsawStructureMixin extends StructureMixin implements StructurifyJigsawStructure
{
	@Unique
	@Nullable
	private Integer structurify$maxDepth = null;

	@Unique
	@Nullable
	private HeightProvider structurify$startHeight = null;

	@Unique
	@Nullable
	private Optional<Heightmap.Types> structurify$projectStartToHeightmap = null;

	@Unique
	@Nullable
	//? if >= 1.21.9 {
	private JigsawStructure.MaxDistance structurify$maxDistanceFromCenter = null;
	//?} else {
	/^private Integer structurify$maxDistanceFromCenter = null;
	^///?}

	@Override
	public void structurify$setStructureIdentifier(Identifier structureSetIdentifier) {
		super.structurify$setStructureIdentifier(structureSetIdentifier);
		this.invalidateStructureJigsawData();
	}

	@Unique
	@Nullable
	public Integer structurify$getMaxDepth() {
		return this.structurify$maxDepth;
	}

	@Unique
	public void structurify$setMaxDepth(Integer maxDepth) {
		this.structurify$maxDepth = maxDepth;
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Lcom/yungnickyoung/minecraft/yungsapi/world/structure/YungJigsawStructure;maxDepth:I",
			opcode = Opcodes.GETFIELD
		)
	)
	public int structurify$findGenerationPointGetMaxDepth(int originalMaxDepth) {
		return this.structurify$getMaxDepth(originalMaxDepth);
	}

	@Unique
	@Nullable
	public HeightProvider structurify$getStartHeight() {
		return this.structurify$startHeight;
	}

	@Unique
	public void structurify$setStartHeight(@Nullable HeightProvider startHeight) {
		this.structurify$startHeight = startHeight;
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Lcom/yungnickyoung/minecraft/yungsapi/world/structure/YungJigsawStructure;startHeight:Lnet/minecraft/world/level/levelgen/heightproviders/HeightProvider;",
			opcode = Opcodes.GETFIELD
		)
	)
	protected HeightProvider structurify$findGenerationPointGetStartHeight(HeightProvider originalStartHeight) {
		return this.structurify$getStartHeight(originalStartHeight);
	}

	@Unique
	@Nullable
	public Optional<Heightmap.Types> structurify$getProjectStartToHeightmap() {
		return this.structurify$projectStartToHeightmap;
	}

	@Unique
	public void structurify$setProjectStartToHeightmap(@Nullable Optional<Heightmap.Types> projectStartToHeightmap) {
		this.structurify$projectStartToHeightmap = projectStartToHeightmap;
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Lcom/yungnickyoung/minecraft/yungsapi/world/structure/YungJigsawStructure;projectStartToHeightmap:Ljava/util/Optional;",
			opcode = Opcodes.GETFIELD
		)
	)
	protected Optional<Heightmap.Types> structurify$findGenerationPointGetProjectStartToHeightmap(Optional<Heightmap.Types> originalProjectStartToHeightmap) {
		return this.structurify$getProjectStartToHeightmap(originalProjectStartToHeightmap);
	}

	@Unique
	@Nullable
	//? if >= 1.21.9 {
	public JigsawStructure.MaxDistance structurify$getMaxDistanceFromCenter()
	//?} else {
	/^public Integer structurify$getMaxDistanceFromCenter()
	^///?}
	{
		return this.structurify$maxDistanceFromCenter;
	}

	//? if >= 1.21.9 {
	public void structurify$setMaxDistanceFromCenter(@Nullable JigsawStructure.MaxDistance maxDistanceFromCenter)
	//?} else {
	/^public void structurify$setMaxDistanceFromCenter(@Nullable Integer maxDistanceFromCenter)
	^///?}
	{
		this.structurify$maxDistanceFromCenter = maxDistanceFromCenter;
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Lcom/yungnickyoung/minecraft/yungsapi/world/structure/YungJigsawStructure;maxDistanceFromCenter:I",
			opcode = Opcodes.GETFIELD
		)
	)
	protected int structurify$findGenerationPointGetMaxDistanceFromCenter(int originalMaxDistanceFromCenter)
	{
		//? if >= 1.21.9 {
		return this.structurify$getMaxDistanceFromCenter(new JigsawStructure.MaxDistance(originalMaxDistanceFromCenter)).horizontal();
		//?} else {
		/^return this.structurify$getMaxDistanceFromCenter((Integer) originalMaxDistanceFromCenter);
		^///?}
	}
}
*///?}
