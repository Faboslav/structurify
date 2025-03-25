package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.image.impl.AnimatedDynamicTextureImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(value = AnimatedDynamicTextureImage.class, remap = false)
public interface AnimatedDynamicTextureImageAccessor
{
	@Accessor("frameHeight")
	int structurify$getFrameHeight();

	@Accessor("frameWidth")
	int structurify$getFrameWidth();
}
