package com.faboslav.structurify.common.mixin.yacl;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Make search also work with descriptions
 */
@Mixin(value = ControllerWidget.class, remap = false)
public abstract class ControllerWidgetMixin
{
	@Unique
	protected String structurify$optionNameString = "";

	@Unique
	protected String structurify$optionDescriptionString = "";

	@Inject(method = "<init>", at = @At("TAIL"))
	public void structurify$init(Controller control, YACLScreen screen, Dimension dim, CallbackInfo ci) {
		this.structurify$optionNameString = control.option().name().getString().toLowerCase();
		this.structurify$optionDescriptionString = control.option().description().text().getString().toLowerCase();
	}

	@WrapMethod(
		method = "matchesSearch"
	)
	public boolean structurify$matchesSearch(String query, Operation<Boolean> original) {
		if (original.call(query)) {
			return true;
		}

		if (this.structurify$optionNameString == "" || this.structurify$optionDescriptionString == "") {
			return true;
		}

		return this.structurify$optionDescriptionString.contains(query.toLowerCase());
	}
}
