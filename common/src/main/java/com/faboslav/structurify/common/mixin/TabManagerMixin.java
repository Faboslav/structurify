package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.api.StructurifyCategoryTab;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(TabManager.class)
public abstract class TabManagerMixin
{
	@WrapOperation(
		method = "setCurrentTab",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/tabs/Tab;visitChildren(Ljava/util/function/Consumer;)V"
		)
	)
	private void structurify$visitChildren(Tab tab, Consumer<AbstractWidget> consumer, Operation<Void> original) {
		original.call(tab, consumer);

		if (tab instanceof StructurifyCategoryTab categoryTab && categoryTab.structurify$getToggleGroupsButton() != null) {
			consumer.accept(categoryTab.structurify$getToggleGroupsButton());
		}
	}
}
