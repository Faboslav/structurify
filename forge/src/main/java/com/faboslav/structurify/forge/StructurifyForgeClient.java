package com.faboslav.structurify.forge;

import com.faboslav.structurify.common.StructurifyClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public final class StructurifyForgeClient
{
	public static void init(IEventBus modEventBus, IEventBus eventBus) {
		StructurifyClient.init();

		modEventBus.addListener(StructurifyForgeClient::onClientSetup);
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
}
