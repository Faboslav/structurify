//? if cataclysm {
/*package com.faboslav.structurify.neoforge.mixin.compat.cataclysm;

import com.faboslav.structurify.common.api.StructurifyJigsawStructure;
import com.faboslav.structurify.common.mixin.structure.StructureMixin;
import com.github.L_Ender.cataclysm.structures.jisaw.CataclysmJigsawStructure;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

@Pseudo
@Mixin(value = CataclysmJigsawStructure.class, remap = false)
public abstract class CataclysmJigsawStructureMixin extends StructureMixin implements StructurifyJigsawStructure
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
	private Integer structurify$maxDistanceFromCenter = null;

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
			target = "Lcom/github/L_Ender/cataclysm/structures/jisaw/CataclysmJigsawStructure;maxDepth:I",
			opcode = Opcodes.GETFIELD
		),
		require = 0
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
			target = "Lcom/github/L_Ender/cataclysm/structures/jisaw/CataclysmJigsawStructure;startHeight:Lnet/minecraft/world/level/levelgen/heightproviders/HeightProvider;",
			opcode = Opcodes.GETFIELD
		),
		require = 0
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
			target = "Lcom/github/L_Ender/cataclysm/structures/jisaw/CataclysmJigsawStructure;projectStartToHeightmap:Ljava/util/Optional;",
			opcode = Opcodes.GETFIELD
		),
		require = 0
	)
	protected Optional<Heightmap.Types> structurify$findGenerationPointGetProjectStartToHeightmap(Optional<Heightmap.Types> originalProjectStartToHeightmap) {
		return this.structurify$getProjectStartToHeightmap(originalProjectStartToHeightmap);
	}

	@Unique
	@Nullable
	public Integer structurify$getMaxDistanceFromCenter() {
		return this.structurify$maxDistanceFromCenter;
	}

	public void structurify$setMaxDistanceFromCenter(@Nullable Integer maxDistanceFromCenter) {
		this.structurify$maxDistanceFromCenter = maxDistanceFromCenter;
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Lcom/github/L_Ender/cataclysm/structures/jisaw/CataclysmJigsawStructure;maxDistanceFromCenter:I",
			opcode = Opcodes.GETFIELD
		),
		require = 0
	)
	protected int structurify$findGenerationPointGetMaxDistanceFromCenter(int originalMaxDistanceFromCenter)
	{
		return this.structurify$getMaxDistanceFromCenter((Integer) originalMaxDistanceFromCenter);
	}
}
*///?}
