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
		/*WorldRenderEvents.AFTER_ENTITIES.register(StructurifyFabricClient::onRenderLevelStage);
		 *///?}
	}

	//? if >= 26.1 {
	private static void onRenderLevelStage(LevelRenderContext context)
	//?} else {
	/*private static void onRenderLevelStage(WorldRenderContext context)
	 *///?}
	{
		//? if >= 26.1 {
		var cameraPos = context.levelState().cameraRenderState.pos;
		var poseStack = context.poseStack();
		var submitNodeCollector = context.submitNodeCollector();
		//?} else if >= 1.21.11 {
		/*var cameraPos = context.worldState().cameraRenderState.pos;
		var poseStack = context.matrices();
		var submitNodeCollector = context.commandQueue();
		*///?} else if >= 1.21.9 {
		/*var cameraPos = context.worldState().cameraRenderState.pos;
		var poseStack = context.matrices();
		var submitNodeCollector = context.consumers();
		*///?} else if >= 1.21.1 {
		/*var cameraPos = context.camera().getPosition();
		var poseStack = context.matrixStack();
		var submitNodeCollector = context.consumers();
		*///?} else {
		/*var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		var poseStack = context.matrixStack();
		var submitNodeCollector = context.consumers();
		*///?}

		StructurifyClient.getDebugRenderer().render(Minecraft.getInstance(), poseStack, cameraPos, submitNodeCollector);
	}
}