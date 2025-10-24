package com.faboslav.structurify.fabric;

import com.faboslav.structurify.common.StructurifyClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.Minecraft;

//? >= 1.21.9 {
//?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
*///?}

public final class StructurifyFabricClient implements ClientModInitializer
{
	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		StructurifyClient.init();

		//? >= 1.21.9 {
		//?} else {
		/*WorldRenderEvents.AFTER_ENTITIES.register(StructurifyFabricClient::onRenderLevelStage);
		*///?}
	}

	//? >= 1.21.9 {
	// TODO
	//?} else {
	/*private static void onRenderLevelStage(WorldRenderContext context) {
		StructurifyClient.getDebugRenderer().render(Minecraft.getInstance(), context.matrixStack(), context.consumers());
	}
	*///?}
}

