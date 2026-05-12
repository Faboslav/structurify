package com.faboslav.structurify.common.config.data.structure.jigsaw;

import net.minecraft.world.level.levelgen.Heightmap;
import java.util.Optional;

public enum ProjectStartToHeightmap
{
	NONE(Optional.empty()),
	WORLD_SURFACE_WG(Optional.of(Heightmap.Types.WORLD_SURFACE_WG)),
	WORLD_SURFACE(Optional.of(Heightmap.Types.WORLD_SURFACE)),
	OCEAN_FLOOR_WG(Optional.of(Heightmap.Types.OCEAN_FLOOR_WG)),
	OCEAN_FLOOR(Optional.of(Heightmap.Types.OCEAN_FLOOR)),
	MOTION_BLOCKING(Optional.of(Heightmap.Types.MOTION_BLOCKING)),
	MOTION_BLOCKING_NO_LEAVES(Optional.of(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES));

	private final Optional<Heightmap.Types> value;

	ProjectStartToHeightmap(Optional<Heightmap.Types> value) {
		this.value = value;
	}

	public Optional<Heightmap.Types> toDataValue() {
		return this.value;
	}

	public static ProjectStartToHeightmap fromDataValue(Optional<Heightmap.Types> value) {
		return value.map(types -> switch (types) {
			case WORLD_SURFACE_WG -> WORLD_SURFACE_WG;
			case WORLD_SURFACE -> WORLD_SURFACE;
			case OCEAN_FLOOR_WG -> OCEAN_FLOOR_WG;
			case OCEAN_FLOOR -> OCEAN_FLOOR;
			case MOTION_BLOCKING -> MOTION_BLOCKING;
			case MOTION_BLOCKING_NO_LEAVES -> MOTION_BLOCKING_NO_LEAVES;
		}).orElse(NONE);
	}
}