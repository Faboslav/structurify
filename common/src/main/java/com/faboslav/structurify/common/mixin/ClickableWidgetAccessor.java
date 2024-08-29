package com.faboslav.structurify.common.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(AbstractWidget.class)
public interface ClickableWidgetAccessor
{
	@Accessor("height")
	void structurify$setHeight(int height);
}
