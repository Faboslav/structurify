package com.faboslav.structurify.neoforge;

import com.faboslav.structurify.common.StructurifyClient;
import net.minecraft.client.Minecraft;
//? if < 1.21.11 {
/*import net.minecraft.client.renderer.MultiBufferSource;
 *///?}
//? if >= 1.21.11 {
import net.minecraft.client.renderer.SubmitNodeCollector;
//?}
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

//? if >= 26.2 {
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
//?} else {
/*import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
*///?}

//? if < 1.20.6 {
/*import net.neoforged.neoforge.client.ConfigScreenHandler;
 *///?} else {
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
//?}

public final class StructurifyNeoForgeClient
{
	public static void init(IEventBus modEventBus, IEventBus eventBus) {
		StructurifyClient.init();

		modEventBus.addListener(StructurifyNeoForgeClient::onClientSetup);
		eventBus.addListener(StructurifyNeoForgeClient::onRenderLevelStage);
	}

	private static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			//? if < 1.20.6 {
			/*ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> {
				return new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> {
					return StructurifyClient.getConfigScreen(screen);
				});
			});
			*///?} else {
			ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> {
				return (client, screen) -> {
					return StructurifyClient.getConfigScreen(screen);
				};
			});
			//?}
		});
	}

	//? if >= 26.2 {
	public static void onRenderLevelStage(SubmitCustomGeometryEvent event)
	//?} else if >= 26.1 {
	/*public static void onRenderLevelStage(RenderLevelStageEvent.AfterTranslucentBlocks event)
	*///?} else if >= 1.21.8 {
	/*public static void onRenderLevelStage(RenderLevelStageEvent.AfterParticles event)
	*///?} else {
	/*public static void onRenderLevelStage(RenderLevelStageEvent event)
	 *///?}
	{
		//? if < 1.21.8 {
		/*if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			return;
		}
		*///?}

		//? if >= 1.21.10 {
		var cameraPos = event.getLevelRenderState().cameraRenderState.pos;
		//?} else if >= 1.21.1 {
		/*var cameraPos = event.getCamera().getPosition();
		 *///?} else {
		/*var cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		 *///?}

		//? if >= 26.2 {
		SubmitNodeCollector submitNodeCollector = event.getSubmitNodeCollector();
		//?} else if >= 1.21.11 {
		/*SubmitNodeCollector submitNodeCollector = null;
		 *///?} else {
		/*MultiBufferSource submitNodeCollector = null;
		 *///?}

		StructurifyClient.getDebugRenderer().render(Minecraft.getInstance(), event.getPoseStack(), cameraPos, submitNodeCollector);
	}
}