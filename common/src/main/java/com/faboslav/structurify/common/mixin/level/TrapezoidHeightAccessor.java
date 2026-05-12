package com.faboslav.structurify.common.mixin.level;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrapezoidHeight.class)
public interface TrapezoidHeightAccessor
{
	@Accessor
	VerticalAnchor getMinInclusive();

	@Accessor
	VerticalAnchor getMaxInclusive();

	@Accessor
	int getPlateau();
}