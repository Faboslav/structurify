package com.faboslav.structurify.common.mixin.structure;

import com.faboslav.structurify.common.api.StructurifyTemplatePool;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(StructureTemplatePool.class)
public abstract class StructureTemplatePoolMixin implements StructurifyTemplatePool
{
	@Mutable
	@Shadow
	@Final
	private List<Pair<StructurePoolElement, Integer>> rawTemplates;

	@Mutable
	@Shadow
	@Final
	private ObjectArrayList<StructurePoolElement> templates;

	@Shadow
	private int maxSize;

	@Unique
	@Nullable
	private List<Pair<StructurePoolElement, Integer>> structurify$originalRawTemplates = null;

	@Unique
	@Nullable
	private String structurify$structureTemplatePoolId = null;

	@Unique
	private void structurify$snapshotOriginalRawTemplates() {
		if (this.structurify$originalRawTemplates != null) {
			return;
		}

		this.structurify$originalRawTemplates = List.copyOf(this.rawTemplates);
	}

	public List<Pair<StructurePoolElement, Integer>> structurify$getOriginalRawTemplates() {
		this.structurify$snapshotOriginalRawTemplates();

		return this.structurify$originalRawTemplates;
	}

	public List<Pair<StructurePoolElement, Integer>> structurify$getRawTemplates() {
		return this.rawTemplates;
	}

	public void structurify$setRawTemplates(List<Pair<StructurePoolElement, Integer>> rawTemplates) {
		this.structurify$snapshotOriginalRawTemplates();

		ObjectArrayList<StructurePoolElement> templates = new ObjectArrayList<>();

		for (Pair<StructurePoolElement, Integer> rawTemplate : rawTemplates) {
			for (int i = 0; i < rawTemplate.getSecond(); ++i) {
				templates.add(rawTemplate.getFirst());
			}
		}

		this.rawTemplates = List.copyOf(rawTemplates);
		this.templates = templates;
		this.maxSize = Integer.MIN_VALUE;
	}

	public void structurify$setStructureTemplatePoolId(@Nullable String structureTemplatePoolId) {
		this.structurify$structureTemplatePoolId = structureTemplatePoolId;
	}

	@Nullable
	public String structurify$getStructureTemplatePoolId() {
		return this.structurify$structureTemplatePoolId;
	}
}
