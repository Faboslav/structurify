package com.faboslav.structurify.common.mixin;

import com.faboslav.structurify.common.Structurify;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(FileResourcePackProvider.class)
public class FileResourcePackProviderMixin
{
	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(Path packsDir, ResourceType type, ResourcePackSource source, CallbackInfo ci) {
		if (packsDir == null) {
			Structurify.getLogger().info("wEIRD ITS NULL");
		}
	}
}
