package com.faboslav.structurify.common.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractWidget.class)
public interface AbstractWidgetInvoker
{
	//? <1.21 {
	/*@Accessor("height")
	void setHeight(int height);
	*///?}
}
