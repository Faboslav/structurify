package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.SearchFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = SearchFieldWidget.class, remap = false)
public interface SearchFieldWidgetInvoker
{
	@Invoker("update")
	void structurify$invokeUpdate(String query);
}
