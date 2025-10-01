package com.faboslav.structurify.world.level.structure;

import net.minecraft.resources.ResourceLocation;

public final class StructureSectionClaim {
	public final long token;
	public final ResourceLocation structureId;

	public StructureSectionClaim(long token, ResourceLocation structureId) {
		this.token = token;
		this.structureId = structureId;
	}
}
