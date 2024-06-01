package com.faboslav.structurify.forge;

import com.faboslav.structurify.common.Structurify;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Structurify.MOD_ID)
public final class StructurifyForge
{
    public StructurifyForge() {
		Structurify.getLogger().info("StructurifyForge");
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus eventBus = MinecraftForge.EVENT_BUS;

		// ResourcePackProvider
		//RegistryManagerProvider.addResourcePackProvider(((ClientModLoaderAccessor)ClientModLoader))::callBuildPackFinder);
		Structurify.init();

		if (FMLEnvironment.dist == Dist.CLIENT) {
			StructurifyForgeClient.init(modEventBus, eventBus);
		}

		eventBus.addListener(StructurifyForge::onTagsUpdate);
		modEventBus.addListener(StructurifyForge::onResourceLoad);
    }

	private static void onTagsUpdate(TagsUpdatedEvent event) {
		com.faboslav.structurify.common.events.lifecycle.TagsUpdatedEvent.EVENT.invoke(new com.faboslav.structurify.common.events.lifecycle.TagsUpdatedEvent(event.getRegistryAccess(), event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD));
	}

	private static void onResourceLoad(AddPackFindersEvent event) {
		Structurify.getLogger().info("AddPackFindersEvent");
		/*
		var sources = ((AddPackFindersEventAccessor)event).getSources();

		Structurify.getLogger().info(String.valueOf(sources));

		// Wrap the original consumer with our capturing consumer
		CapturingConsumer capturingConsumer = new CapturingConsumer(((AddPackFindersEventAccessor)event).getSources());

		var fap = new CustomResourcePackProvider();
		capturingConsumer.accept(fap);

		// You can now inspect the captured providers
		List<ResourcePackProvider> capturedProviders = capturingConsumer.getCapturedProviders();
		for (ResourcePackProvider provider : capturedProviders) {
			System.out.println("Captured ResourcePackProvider: " + provider);
		}*/
	}
}
