package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.OptionListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = OptionListWidget.ListGroupSeparatorEntry.class)
public abstract class ListGroupSeparatorEntryMixin
{
	@ModifyArg(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Ldev/isxander/yacl3/gui/TextScaledButtonWidget;<init>(Lnet/minecraft/client/gui/screens/Screen;IIIIFLnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/components/Button$OnPress;)V"
		),
		index = 5
	)
	private float structurify$scaleResetListButtonText(float textScale) {
		return 2.0F;
	}
}
