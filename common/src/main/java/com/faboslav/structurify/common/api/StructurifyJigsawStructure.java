package com.faboslav.structurify.common.api;

import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import org.jetbrains.annotations.Nullable;

//? if >= 1.21.9 {
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
//?}

import java.util.Objects;
import java.util.Optional;

public interface StructurifyJigsawStructure extends StructurifyStructure
{
	default void invalidateStructureJigsawData() {
		structurify$setMaxDepth(null);
		structurify$setStartHeight(null);
		structurify$setProjectStartToHeightmap(null);
		structurify$setMaxDistanceFromCenter(null);
	}

	@Nullable Integer structurify$getMaxDepth();
	void structurify$setMaxDepth(@Nullable Integer maxDepth);

	default int structurify$getMaxDepth(int originalMaxDepth) {
		if (this.structurify$getMaxDepth() == null) {
			var structureData = this.structurify$getStructureData();

			if (structureData == null) {
				this.structurify$setMaxDepth(originalMaxDepth);
			} else {
				var size = structureData.getJigsawData().getSize();
				this.structurify$setMaxDepth(Objects.requireNonNullElse(size, originalMaxDepth));
			}
		}

		return this.structurify$getMaxDepth();
	}

	@Nullable HeightProvider structurify$getStartHeight();
	void structurify$setStartHeight(@Nullable HeightProvider startHeight);

	default HeightProvider structurify$getStartHeight(HeightProvider originalStartHeight) {
		if (this.structurify$getStartHeight() == null) {
			var structureData = this.structurify$getStructureData();

			if (structureData == null) {
				this.structurify$setStartHeight(originalStartHeight);
			} else {
				var heightProviderData = structureData.getJigsawData().getHeightProviderData();

				if (heightProviderData == null) {
					this.structurify$setStartHeight(originalStartHeight);
				} else {
					this.structurify$setStartHeight(heightProviderData.toHeightProvider());
				}
			}
		}

		return this.structurify$getStartHeight();
	}

	@Nullable Optional<Heightmap.Types> structurify$getProjectStartToHeightmap();
	void structurify$setProjectStartToHeightmap(@Nullable Optional<Heightmap.Types> projectStartToHeightmap);

	default Optional<Heightmap.Types> structurify$getProjectStartToHeightmap(Optional<Heightmap.Types> originalProjectStartToHeightmap) {
		if (this.structurify$getProjectStartToHeightmap() == null) {
			var structureData = this.structurify$getStructureData();

			if (structureData == null) {
				this.structurify$setProjectStartToHeightmap(originalProjectStartToHeightmap);
			} else {
				var projectStartToHeightmap = structureData.getJigsawData().getProjectStartToHeightmap();

				if(projectStartToHeightmap == null) {
					this.structurify$setProjectStartToHeightmap(originalProjectStartToHeightmap);
				} else {
					this.structurify$setProjectStartToHeightmap(projectStartToHeightmap.toDataValue());
				}
			}
		}

		return this.structurify$getProjectStartToHeightmap();
	}

	//? if >= 1.21.9 {
	@Nullable JigsawStructure.MaxDistance structurify$getMaxDistanceFromCenter();
	//?} else {
	/*@Nullable Integer structurify$getMaxDistanceFromCenter();
	*///?}

	//? if >= 1.21.9 {
	void structurify$setMaxDistanceFromCenter(@Nullable JigsawStructure.MaxDistance maxDistanceFromCenter);
	 //?} else {
	/*void structurify$setMaxDistanceFromCenter(@Nullable Integer maxDistanceFromCenter);
	*///?}

	//? if >= 1.21.9 {
	default JigsawStructure.MaxDistance structurify$getMaxDistanceFromCenter(JigsawStructure.MaxDistance originalMaxDistanceFromCenter)
	//?} else {
	/*default int structurify$getMaxDistanceFromCenter(Integer originalMaxDistanceFromCenter)
	*///?}
	{
		if(this.structurify$getMaxDistanceFromCenter() == null) {
			var structureData = this.structurify$getStructureData();

			if (structureData == null) {
				this.structurify$setMaxDistanceFromCenter(originalMaxDistanceFromCenter);
			} else {
				var verticalMaxDistanceFromCenter = structureData.getJigsawData().getVerticalMaxDistanceFromCenter();
				var horizontalMaxDistanceFromCenter = structureData.getJigsawData().getVerticalMaxDistanceFromCenter();

				if(verticalMaxDistanceFromCenter == null || horizontalMaxDistanceFromCenter == null) {
					this.structurify$setMaxDistanceFromCenter(originalMaxDistanceFromCenter);
				} else {
					//? if >= 1.21.9 {
					this.structurify$setMaxDistanceFromCenter(new JigsawStructure.MaxDistance(
						structureData.getJigsawData().getHorizontalMaxDistanceFromCenter(),
						structureData.getJigsawData().getVerticalMaxDistanceFromCenter()
					));
					//?} else {
					/*this.structurify$setMaxDistanceFromCenter(horizontalMaxDistanceFromCenter);
					*///?}
				}
			}
		}

		return this.structurify$getMaxDistanceFromCenter();
	}
}
