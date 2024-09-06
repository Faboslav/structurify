package com.faboslav.structurify.neoforge;

import com.faboslav.structurify.common.StructurifyClient;
import com.faboslav.structurify.common.config.client.gui.StructurifyConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/*? if <1.20.6 {*/
import net.neoforged.neoforge.client.ConfigScreenHandler;
 
/*?} else {*/
/*import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
	*//*?}*/

@OnlyIn(Dist.CLIENT)
public final class StructurifyNeoForgeClient
{
	public static void init(IEventBus modEventBus, IEventBus eventBus) {
		StructurifyClient.init();

		modEventBus.addListener(StructurifyNeoForgeClient::onClientSetup);
	}

	private static void onClientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			if (ModList.get().isLoaded("yet_another_config_lib_v3")) {
				/*? if <1.20.6 {*/
                ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                        new ConfigScreenHandler.ConfigScreenFactory(
                                (mc, screen) -> new StructurifyConfigScreen(screen)
                        )
                );
                /*?} else {*/
				/*ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (client, screen) -> {
					return new StructurifyConfigScreen(screen);
				});
				*//*?}*/
			}
		});
	}
}
