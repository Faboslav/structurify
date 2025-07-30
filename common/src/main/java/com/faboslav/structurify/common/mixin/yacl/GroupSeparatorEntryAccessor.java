package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.gui.OptionListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = OptionListWidget.GroupSeparatorEntry.class, remap = false)
public interface GroupSeparatorEntryAccessor
{
	@Accessor
	OptionGroup getGroup();
}