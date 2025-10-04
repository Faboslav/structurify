package com.faboslav.structurify.forge;

import com.faboslav.structurify.common.StructurifyClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings({"all", "deprecated", "removal"})
public final class StructurifyForgeClient
{
	public static void init(IEventBus modEventBus, IEventBus eventBus) {
		StructurifyClient.init();

		modEventBus.addListener(StructurifyForgeClient::onClientSetup);

		eventBus.addListener(StructurifyForgeClient::onRenderLevelStage);
	}

	private static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
				new ConfigScreenHandler.ConfigScreenFactory(
					(mc, screen) -> StructurifyClient.getConfigScreen(screen)
				)
			);
		});
	}

	private static void onRenderLevelStage(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

		StructurifyClient.getDebugRenderer().render(Minecraft.getInstance(), event.getPoseStack(), null);
	}
}
