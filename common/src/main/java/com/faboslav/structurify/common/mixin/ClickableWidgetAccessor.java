package com.faboslav.structurify.common.mixin;

import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(ClickableWidget.class)
public interface ClickableWidgetAccessor
{
    @Accessor("height")
    void structurify$setHeight(int height);
}
