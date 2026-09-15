package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.api.StructurifyCategoryTab;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Consumer;

@Mixin(TabManager.class)
public abstract class TabManagerMixin
{
	@Shadow
	@Final
	private Consumer<AbstractWidget> addWidget;

	@Shadow
	@Final
	private Consumer<AbstractWidget> removeWidget;

	@Shadow
	@Nullable
	private Tab currentTab;

	@WrapMethod(
		//? if >= 26.2 {
		method = "setCurrentTab(Lnet/minecraft/client/gui/components/tabs/Tab;ZZ)V"
		//?} else {
		/*method = "setCurrentTab(Lnet/minecraft/client/gui/components/tabs/Tab;Z)V"
		*///?}
	)
	private void structurify$setCurrentTab(
		Tab tab,
		boolean playClickSound,
		//? if >= 26.2 {
		boolean flag,
		//?}
		Operation<Void> original
	) {
		var previousTab = this.currentTab;

		//? if >= 26.2 {
		original.call(tab, playClickSound, flag);
		//?} else {
		/*original.call(tab, playClickSound);
		*///?}

		if (this.currentTab == previousTab) {
			return;
		}

		if (previousTab instanceof StructurifyCategoryTab previousCategoryTab && previousCategoryTab.structurify$getToggleGroupsButton() != null) {
			this.removeWidget.accept(previousCategoryTab.structurify$getToggleGroupsButton());
		}

		if (this.currentTab instanceof StructurifyCategoryTab currentCategoryTab && currentCategoryTab.structurify$getToggleGroupsButton() != null) {
			this.addWidget.accept(currentCategoryTab.structurify$getToggleGroupsButton());
		}
	}
}
