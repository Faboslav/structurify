package com.faboslav.structurify.common.mixin.structure.jigsaw.compat;

import org.spongepowered.asm.mixin.Mixin;

//? if repurposed_structures {
/*import com.faboslav.structurify.common.api.StructurifyJigsawStructure;
import com.faboslav.structurify.common.mixin.structure.StructureMixin;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.telepathicgrunt.repurposedstructures.world.structures.GenericJigsawStructure;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

//? if >= 1.21.9 {
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
//?}

@Mixin(value = GenericJigsawStructure.class)
public abstract class RepurposedStructuresGenericJigsawStructureMixin extends StructureMixin implements StructurifyJigsawStructure
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
	private Optional<Integer> structurify$maxDistanceFromCenter = null;

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
			target = "Lcom/telepathicgrunt/repurposedstructures/world/structures/GenericJigsawStructure;size:I",
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
			target = "Lcom/telepathicgrunt/repurposedstructures/world/structures/GenericJigsawStructure;startHeight:Lnet/minecraft/world/level/levelgen/heightproviders/HeightProvider;",
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
			target = "Lcom/telepathicgrunt/repurposedstructures/world/structures/GenericJigsawStructure;projectStartToHeightmap:Ljava/util/Optional;",
			opcode = Opcodes.GETFIELD
		)
	)
	protected Optional<Heightmap.Types> structurify$findGenerationPointGetProjectStartToHeightmap(Optional<Heightmap.Types> originalProjectStartToHeightmap) {
		return this.structurify$getProjectStartToHeightmap(originalProjectStartToHeightmap);
	}

	@ModifyExpressionValue(
		method = "findGenerationPoint",
		at = @At(
			value = "FIELD",
			target = "Lcom/telepathicgrunt/repurposedstructures/world/structures/GenericJigsawStructure;maxDistanceFromCenter:Ljava/util/Optional;",
			opcode = Opcodes.GETFIELD
		)
	)
	protected Optional<Integer> structurify$findGenerationPointGetMaxDistanceFromCenter(Optional<Integer> originalMaxDistanceFromCenter)
	{
		if(this.structurify$maxDistanceFromCenter == null) {
			var structureData = this.structurify$getStructureData();

			if (structureData == null) {
				this.structurify$maxDistanceFromCenter = originalMaxDistanceFromCenter;
			} else {
				var verticalMaxDistanceFromCenter = structureData.getJigsawData().getVerticalMaxDistanceFromCenter();
				var horizontalMaxDistanceFromCenter = structureData.getJigsawData().getVerticalMaxDistanceFromCenter();

				if(verticalMaxDistanceFromCenter == null || horizontalMaxDistanceFromCenter == null) {
					this.structurify$maxDistanceFromCenter = originalMaxDistanceFromCenter;
				} else {
					this.structurify$maxDistanceFromCenter = Optional.of(horizontalMaxDistanceFromCenter);
				}
			}
		}

		return this.structurify$maxDistanceFromCenter;
	}
}
*///?} else {
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

@Mixin(value = JigsawStructure.class)
public abstract class RepurposedStructuresGenericJigsawStructureMixin
{
}
//?}
