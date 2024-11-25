package com.faboslav.structurify.forge;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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

		eventBus.addListener(StructurifyForge::onResourceManagerReload);
		eventBus.addListener(StructurifyForge::onServerAboutToStart);
	}

	private static void onResourceManagerReload(TagsUpdatedEvent event) {
		if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
			return;
		}

		StructurifyRegistryManagerProvider.setRegistryManager(event.getRegistryAccess());
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
	}

	private static void onServerAboutToStart(ServerAboutToStartEvent event) {
		StructurifyRegistryManagerProvider.setRegistryManager(event.getServer().registryAccess());
		UpdateRegistriesEvent.EVENT.invoke(new UpdateRegistriesEvent(event.getServer().registryAccess()));
	}
}
