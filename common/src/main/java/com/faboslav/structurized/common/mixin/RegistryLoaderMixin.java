package com.faboslav.structurized.common.mixin;

import com.faboslav.structurized.common.Structurized;
import com.google.gson.JsonElement;
import com.mojang.serialization.Decoder;
import net.minecraft.registry.*;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

@Mixin(RegistryLoader.class)
public abstract class RegistryLoaderMixin
{
	@Inject(
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/Decoder;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"),
		method = "load(Lnet/minecraft/registry/RegistryOps$RegistryInfoGetter;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/registry/MutableRegistry;Lcom/mojang/serialization/Decoder;Ljava/util/Map;)V",
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static <E> void load(
		RegistryOps.RegistryInfoGetter registryInfoGetter,
		ResourceManager resourceManager,
		RegistryKey<? extends Registry<E>> registryRef,
		MutableRegistry<E> newRegistry,
		Decoder<E> decoder,
		Map<RegistryKey<?>, Exception> exceptions,
		CallbackInfo ci,
		String string,
		ResourceFinder resourceFinder,
		RegistryOps registryOps,
		Iterator var9,
		Map.Entry entry,
		Identifier identifier,
		RegistryKey registryKey,
		Resource resource,
		Reader reader,
		JsonElement jsonElement
	) {
		if(!Structurized.getConfig().isLoaded) {
			return;
		}

		if (!string.equals("worldgen/structure_set") || jsonElement.getAsJsonObject().getAsJsonObject("placement").get("type").getAsString().equals("minecraft:concentric_rings")) {
			return;
		}

		var placementJson = jsonElement.getAsJsonObject().getAsJsonObject("placement");

		if(placementJson == null) {
			return;
		}

		var placementType = placementJson.get("type").getAsString();

		if(placementType == "minecraft:concentric_rings") {
			return;
		}

		if(!placementJson.has("spacing") || !placementJson.has("separation")) {
			return;
		}

		Structurized.getLogger().info("5: " + registryKey.getValue().toString());

		var structureSetId = registryKey.getValue().toString();

		//if(Structurized.getConfig().getStructureSetData().containsKey())


		/*
		var defaultSpacing = placementJson.get("spacing").getAsInt();
		var defaultSeparation = placementJson.get("separation").getAsInt();

		int defaultSpacing;
			int separation;

			if (jsonElement.getAsJsonObject().getAsJsonObject("placement").get("spacing") == null) spacing = 1;
			else spacing = (int)(Math.min(jsonElement.getAsJsonObject().getAsJsonObject("placement").get("spacing").getAsDouble() * factor, 4096.0));
			if (jsonElement.getAsJsonObject().getAsJsonObject("placement").get("separation") == null) separation = 1;
			else separation = (int)(Math.min(jsonElement.getAsJsonObject().getAsJsonObject("placement").get("separation").getAsDouble() * factor, 4096.0));
			if (separation >= spacing) {
				if (spacing == 0) spacing = 1;
				separation = spacing - 1;
			}

			jsonElement.getAsJsonObject().getAsJsonObject("placement").addProperty("spacing", spacing);
			jsonElement.getAsJsonObject().getAsJsonObject("placement").addProperty("separation", separation);*/
	}
}
