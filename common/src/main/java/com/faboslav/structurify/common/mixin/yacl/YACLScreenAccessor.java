package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = YACLScreen.class, remap = false)
public interface YACLScreenAccessor
{
	@Accessor
	Screen getParent();
}
