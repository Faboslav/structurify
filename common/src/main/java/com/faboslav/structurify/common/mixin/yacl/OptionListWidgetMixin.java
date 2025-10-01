package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OptionListWidget.class, remap = false)
public class OptionListWidgetMixin
{
	@Shadow
	@Final
	private YACLScreen yaclScreen;

	/**
	 * This sets scroll position back to 0 so search result is actually visible on screen rather than being off screen
	 */
	@Inject(
		method = "updateSearchQuery",
		at = @At("TAIL")
	)
	public void structurify$updateSearchQuery(CallbackInfo ci) {
		var currentTab = yaclScreen.tabNavigationBar.getTabManager().getCurrentTab();

		if (currentTab instanceof YACLScreen.CategoryTab yaclScreenCategoryTab) {
			var categoryTab = ((CategoryTabAccessor) yaclScreenCategoryTab);
			//? >=1.21.9 {
			var optionListWidget = categoryTab.getOptionList().getType();
			//?} else {
			/*var optionListWidget = categoryTab.getOptionList().getList();
			 *///?}
			optionListWidget.setScrollAmount(0);
		}
	}
}
