package com.faboslav.structurify.common.mixin.yacl;

import com.faboslav.structurify.common.StructurifyClient;
import com.faboslav.structurify.common.api.StructurifyYACLScreen;
import com.faboslav.structurify.common.versions.VersionedGui;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = YACLScreen.class)
public abstract class YACLScreenMixin extends Screen implements StructurifyYACLScreen
{
	@Unique
	private boolean structurify$isStructurifyScreen = false;

	protected YACLScreenMixin(Component component) {
		super(component);
	}

	@Override
	public boolean structurify$isStructurifyScreen() {
		return this.structurify$isStructurifyScreen;
	}

	@Override
	public void structurify$markAsStructurifyScreen() {
		this.structurify$isStructurifyScreen = true;
	}

	@Inject(
		method = "onClose",
		at = @At("HEAD")
	)
	public void structurify$onCloseHead(CallbackInfo ci) {
		if (this.minecraft != null) {
			if (VersionedGui.getScreen(this.minecraft) instanceof YACLScreen yaclsScreen) {
				StructurifyClient.getConfigScreen().saveScreenState(yaclsScreen);
			}
		}
	}

	@Inject(
		method = "onClose",
		at = @At("TAIL")
	)
	public void structurify$onCloseTail(CallbackInfo ci) {
		if (this.minecraft != null) {
			if (VersionedGui.getScreen(this.minecraft) instanceof YACLScreen yaclsScreen) {
				StructurifyClient.getConfigScreen().loadScreenState(yaclsScreen);
			}
		}
	}
}
