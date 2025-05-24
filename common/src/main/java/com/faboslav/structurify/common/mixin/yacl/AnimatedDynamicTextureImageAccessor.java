package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AnimatedDynamicTextureImage.class)
public interface AnimatedDynamicTextureImageAccessor
{
	@Accessor("frameHeight")
	int structurify$getFrameHeight();

	@Accessor("frameWidth")
	int structurify$getFrameWidth();
}
