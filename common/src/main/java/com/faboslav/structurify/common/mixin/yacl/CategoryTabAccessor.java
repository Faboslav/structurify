package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.SearchFieldWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.tab.ListHolderWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = YACLScreen.CategoryTab.class, remap = false)
public interface CategoryTabAccessor
{
	@Accessor
	ListHolderWidget<OptionListWidget> getOptionList();

	@Accessor
	SearchFieldWidget getSearchField();
}
