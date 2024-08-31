package com.faboslav.structurify.forge;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.PrepareRegistriesEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Structurify.MOD_ID)
public final class StructurifyForge
{
	public StructurifyForge() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus eventBus = MinecraftForge.EVENT_BUS;

		Structurify.init();

		if (FMLEnvironment.dist == Dist.CLIENT) {
			StructurifyForgeClient.init(modEventBus, eventBus);
		}

		modEventBus.addListener(StructurifyForge::onCommonSetup);

		eventBus.addListener(StructurifyForge::onServerStart);
		eventBus.addListener(StructurifyForge::onTagsUpdate);
	}

	private static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
		});
	}

	private static void onServerStart(ServerAboutToStartEvent event) {
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
		PrepareRegistriesEvent.EVENT.invoke(new PrepareRegistriesEvent(event.getServer().registryAccess().freeze()));
	}

	private static void onTagsUpdate(TagsUpdatedEvent event) {
		PrepareRegistriesEvent.EVENT.invoke(new PrepareRegistriesEvent(event.getRegistryAccess().freeze()));
	}
}
