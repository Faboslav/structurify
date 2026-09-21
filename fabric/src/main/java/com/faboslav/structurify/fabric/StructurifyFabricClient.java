package com.faboslav.structurify.fabric;

import com.faboslav.structurify.common.StructurifyClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

//? if >= 26.1 {
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
//?} else if >= 1.21.9 {
/*import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
*///?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
*///?}

public final class StructurifyFabricClient implements ClientModInitializer
{
	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		StructurifyClient.init();

		//? if >= 26.1 {
		LevelRenderEvents.BEFORE_GIZMOS.register(StructurifyFabricClient::onRenderLevelStage);
		 //?} else {
		//WorldRenderEvents.AFTER_ENTITIES.register(StructurifyFabricClient::onRenderLevelStage);
		//?}
	}

	//? if >= 26.1 {
	private static void onRenderLevelStage(LevelRenderContext context)
	//?} else {
	//private static void onRenderLevelStage(WorldRenderContext context)
	 //?}
	{
		//? if >= 26.1 {
		var cameraPos = context.levelState().cameraRenderState.pos;
		StructurifyClient.getDebugRenderer().render(Minecraft.getInstance(), cameraPos);
		//?} else if >= 1.21.11 {
		/*var cameraPos = context.worldState().cameraRenderState.pos;
		StructurifyClient.getDebugRenderer().render(Minecraft.getInstance(), cameraPos);
		*///?} else if >= 1.21.9 {
		/*var cameraPos = context.worldState().cameraRenderState.pos;
		StructurifyClient.getDebugRenderer().render(Minecraft.getInstance(), context.matrices(), cameraPos, context.consumers());
		*///?} else if >= 1.21.1 {
		/*var cameraPos = context.camera().getPosition();
		StructurifyClient.getDebugRenderer().render(Minecraft.getInstance(), context.matrixStack(), cameraPos, context.consumers());
		*///?} else {
		/*var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		StructurifyClient.getDebugRenderer().render(Minecraft.getInstance(), context.matrixStack(), cameraPos, context.consumers());
		*///?}
	}
}
