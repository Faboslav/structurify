package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.client.ClientLoadedEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
	@Shadow
	private Overlay overlay;

	@Inject(method = "setOverlay", at = @At("HEAD"))
	private void ding_setOverlay(@Nullable Overlay newOverlay, CallbackInfo ci) {
		if (overlay instanceof SplashOverlay && newOverlay == null) {
			Structurify.getLogger().info("LOADING!");
		}
	}
}