package com.faboslav.structurify.common.mixin.level;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VeryBiasedToBottomHeight.class)
public interface VeryBiasedToBottomHeightAccessor
{
	@Accessor
	VerticalAnchor getMinInclusive();

	@Accessor
	VerticalAnchor getMaxInclusive();

	@Accessor
	int getInner();
}