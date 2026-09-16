package com.faboslav.structurify.common.mixin.yacl;

import com.faboslav.structurify.common.api.StructurifyCategoryTab;
import com.faboslav.structurify.common.api.StructurifyYACLScreen;
import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OptionListWidget.GroupSeparatorEntry.class, remap = false)
public abstract class GroupSeparatorEntryMixin
{
	@Shadow
	@Final
	protected Screen screen;

	@Inject(
		method = "setExpanded",
		at = @At("TAIL")
	)
	private void structurify$setExpanded(boolean expanded, CallbackInfo ci) {
		if (!(this.screen instanceof YACLScreen yaclScreen) || !((StructurifyYACLScreen) yaclScreen).structurify$isStructurifyScreen() || yaclScreen.tabNavigationBar == null) {
			return;
		}

		if (yaclScreen.tabNavigationBar.getTabManager().getCurrentTab() instanceof StructurifyCategoryTab categoryTab) {
			categoryTab.structurify$updateToggleGroupsButton();
		}
	}
}
