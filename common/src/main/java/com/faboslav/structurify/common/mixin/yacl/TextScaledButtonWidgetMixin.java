package com.faboslav.structurify.common.mixin.yacl;

import dev.isxander.yacl3.gui.TextScaledButtonWidget;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

//? if >= 1.21.11 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.TextAlignment;
import org.spongepowered.asm.mixin.Shadow;
//?}

@Mixin(value = TextScaledButtonWidget.class)
public abstract class TextScaledButtonWidgetMixin extends AbstractButton
{
	protected TextScaledButtonWidgetMixin(int x, int y, int width, int height, Component message) {
		super(x, y, width, height, message);
	}

	//? if >= 1.21.11 {
	@Shadow(remap = false)
	public float textScale;

	@WrapMethod(
		//? if >= 26.1 {
		method = "extractDefaultLabel"
		//?} else {
		/*method = "renderDefaultLabel"
		*///?}
	)
	private void structurify$scaleDefaultLabel(ActiveTextCollector textCollector, Operation<Void> original) {
		if (this.textScale == 1.0F) {
			original.call(textCollector);
			return;
		}

		var centerX = this.getX() + this.getWidth() / 2.0F;
		var centerY = this.getY() + this.getHeight() / 2.0F;
		var x = Math.round(centerX / this.textScale);
		var y = Math.round(centerY / this.textScale - 4.5F);

		textCollector.accept(TextAlignment.CENTER, x, y, textCollector.defaultParameters().withScale(this.textScale), this.getMessage());
	}
	//?}
}
