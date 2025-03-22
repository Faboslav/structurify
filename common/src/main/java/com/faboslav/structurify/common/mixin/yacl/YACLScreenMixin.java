package com.faboslav.structurify.common.mixin.yacl;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.StructurifyClient;
import dev.isxander.yacl3.gui.YACLScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Environment(EnvType.CLIENT)
@Mixin(value = YACLScreen.class, remap = false)
public abstract class YACLScreenMixin extends Screen
{
	protected YACLScreenMixin(Component component) {
		super(component);
	}

	@Inject(
		method = "onClose",
		at = @At("HEAD")
	)
	public void structurify$onCloseHead(CallbackInfo ci) {
		if(this.minecraft != null) {
			if(this.minecraft.screen instanceof YACLScreen yaclsScreen) {
				var configScreen = StructurifyClient.getConfigScreen();
				configScreen.saveScreenState(yaclsScreen);
			}
		}
	}

	@Inject(
		method = "onClose",
		at = @At("TAIL")
	)
	public void structurify$onCloseTail(CallbackInfo ci) {
		if(this.minecraft != null) {
			if(this.minecraft.screen instanceof YACLScreen yaclsScreen) {
				var configScreen = StructurifyClient.getConfigScreen();
				configScreen.loadScreenState(yaclsScreen);
			}
		}
	}
}
