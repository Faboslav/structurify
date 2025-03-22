package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.SearchFieldWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(value = SearchFieldWidget.class, remap = false)
public interface SearchFieldWidgetInvoker
{
	@Invoker("update")
	void structurify$invokeUpdate(String query);
}
