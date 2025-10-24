package com.faboslav.structurify.common.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;

//? if <1.21 {
import org.spongepowered.asm.mixin.gen.Accessor;
//?}

@Mixin(AbstractWidget.class)
public interface AbstractWidgetInvoker
{
	//? if <1.21 {
	@Accessor("height")
	void setHeight(int height);
	//?}
}
