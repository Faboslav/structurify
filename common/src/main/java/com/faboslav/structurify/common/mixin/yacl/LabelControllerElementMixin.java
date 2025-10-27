package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.controllers.LabelController;
import org.spongepowered.asm.mixin.Mixin;

//? if < 1.21.1 {
/*import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Shadow;
*///?}

@Mixin(value = LabelController.LabelControllerElement.class)
public abstract class LabelControllerElementMixin
{
	//? if < 1.21.1 {
	/*@Shadow
	@Nullable
	protected abstract Style getStyle(int mouseX, int mouseY);

	@WrapMethod(
		method = "mouseClicked"
	)
	private boolean structurify$getClickEvent(
		double mouseX,
		double mouseY,
		int button,
		Operation<Boolean> original
	) {
		Style style = this.getStyle((int) mouseX, (int) mouseY);

		if (style == null) {
			return false;
		}

		return original.call(mouseX, mouseY, button);
	}
	*///?}
}
