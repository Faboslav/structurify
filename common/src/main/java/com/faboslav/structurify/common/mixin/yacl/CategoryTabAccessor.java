package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.SearchFieldWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? >=1.21.9 {
import dev.isxander.yacl3.gui.WidgetAndType;
//?} else {
/*import dev.isxander.yacl3.gui.tab.ListHolderWidget;
*///?}

@Mixin(value = YACLScreen.CategoryTab.class, remap = false)
public interface CategoryTabAccessor
{
	@Accessor
	//? >=1.21.9 {
	WidgetAndType<OptionListWidget> getOptionList();
	//?} else {
	/*ListHolderWidget<OptionListWidget> getOptionList();
	*///?}

	@Accessor
	SearchFieldWidget getSearchField();
}
