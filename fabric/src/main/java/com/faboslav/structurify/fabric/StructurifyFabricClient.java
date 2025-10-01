package com.faboslav.structurify.fabric;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.StructurifyClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;

public final class StructurifyFabricClient implements ClientModInitializer
{
	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		StructurifyClient.init();

		WorldRenderEvents.AFTER_ENTITIES.register(StructurifyFabricClient::onRenderLevelStage);
	}

	private static void onRenderLevelStage(WorldRenderContext context) {
		Structurify.getDebugRenderer().render(Minecraft.getInstance(), context.matrixStack(), context.consumers());
	}
}

