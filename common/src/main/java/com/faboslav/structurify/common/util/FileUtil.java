package com.faboslav.structurify.common.util;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.platform.ModIconInfo;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileUtil
{
	public static Optional<ModIconInfo> getModIconInfo(String id, Optional<String> iconPath, Optional<Path> path) {
		try(var stream = Files.newInputStream(path.orElseThrow())) {
			return getModIconInfo(id, iconPath, stream);
		} catch(Exception exception) {
			return Optional.empty();
		}
	}

	public static Optional<ModIconInfo> getModIconInfo(String id, Optional<String> iconPath, InputStream stream) {
		try {
			var image = NativeImage.read(stream);
			var width = image.getWidth();
			var height = image.getHeight();
			//? if >= 1.21.5 {
			var texture = new DynamicTexture(() -> "mod_icon/" + id, image);
			//?} else {
			/*var texture = new DynamicTexture(image);
			 *///?}
			var textureId = Structurify.makeId(id, iconPath.get());
			Minecraft.getInstance()
				.getTextureManager()
				.register(textureId, texture);
			return Optional.of(new ModIconInfo(textureId, width, height));
		} catch(Exception exception) {
			return Optional.empty();
		}
	}
}
