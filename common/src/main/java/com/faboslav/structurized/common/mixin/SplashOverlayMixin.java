package com.faboslav.structurized.common.mixin;

import com.faboslav.structurized.common.Structurized;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.SplashOverlay;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin {
	@Shadow
	@Final
	private boolean reloading;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V"))
	public void plingWhenResourcesReloaded(CallbackInfo ci) {
		if (!this.reloading) {
			Structurized.getConfig().load();
		}
	}
}