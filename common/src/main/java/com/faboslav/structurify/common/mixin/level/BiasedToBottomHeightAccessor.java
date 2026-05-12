package com.faboslav.structurify.common.mixin.level;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.BiasedToBottomHeight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiasedToBottomHeight.class)
public interface BiasedToBottomHeightAccessor
{
	@Accessor
	VerticalAnchor getMinInclusive();

	@Accessor
	VerticalAnchor getMaxInclusive();

	@Accessor
	int getInner();
}