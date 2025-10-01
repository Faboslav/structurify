package com.faboslav.structurify.neoforge;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.StructurifyClient;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
//? <1.20.6 {
/*import net.neoforged.neoforge.client.ConfigScreenHandler;

 *///?} else {
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
//?}

@OnlyIn(Dist.CLIENT)
public final class StructurifyNeoForgeClient
{
	public static void init(IEventBus modEventBus, IEventBus eventBus) {
		StructurifyClient.init();

		modEventBus.addListener(StructurifyNeoForgeClient::onClientSetup);

		eventBus.addListener(StructurifyNeoForgeClient::onRenderLevelStage);
	}

	private static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			//? <1.20.6 {
			/*ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
				new ConfigScreenHandler.ConfigScreenFactory(
					(mc, screen) -> StructurifyClient.getConfigScreen(screen)
				)
			);
			*///?} else {
			ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, screen) -> {
				return StructurifyClient.getConfigScreen(screen);
			});
			//?}
		});
	}

	//? >= 1.21.8 {
	/*public static void onRenderLevelStage(RenderLevelStageEvent.AfterParticles event)
	*///?} else {
	public static void onRenderLevelStage(RenderLevelStageEvent event)
	 //?}
	{
		//? < 1.21.8 {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			return;
		}
		//?}

		Structurify.getDebugRenderer().render(Minecraft.getInstance(), event.getPoseStack(), null);
	}
}
