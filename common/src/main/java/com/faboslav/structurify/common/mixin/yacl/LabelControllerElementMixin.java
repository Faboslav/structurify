package com.faboslav.structurify.common.mixin.yacl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.isxander.yacl3.gui.controllers.LabelController;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = LabelController.LabelControllerElement.class)
public abstract class LabelControllerElementMixin
{
	@Shadow
	@Nullable
	protected abstract Style getStyle(int mouseX, int mouseY);

	@WrapMethod(
		//? if >= 1.21.9 {
		method = "onMouseClicked",
		remap = false
		//?} else {
		/*method = "mouseClicked"
		*///?}
	)
	private boolean structurify$getClickEvent(
		double mouseX,
		double mouseY,
		int button,
		Operation<Boolean> original
	) {
		Style style = this.getStyle((int)mouseX, (int)mouseY);

		if(style == null) {
			return false;
		}

		return original.call(mouseX, mouseY, button);
	}
}
