package com.faboslav.structurify.neoforge;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

@Mod(Structurify.MOD_ID)
public final class StructurifyNeoForge
{
	public StructurifyNeoForge(ModContainer modContainer, IEventBus modEventBus) {
		var eventBus = NeoForge.EVENT_BUS;

		Structurify.init();

		if (FMLEnvironment.dist == Dist.CLIENT) {
			StructurifyNeoForgeClient.init(modEventBus, eventBus);
		}

		eventBus.addListener(StructurifyNeoForge::onResourceManagerReload);
		eventBus.addListener(StructurifyNeoForge::onServerAboutToStart);
	}

	private static void onResourceManagerReload(AddReloadListenerEvent event) {
		StructurifyRegistryManagerProvider.setRegistryManager(event.getRegistryAccess());
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
	}

	private static void onServerAboutToStart(ServerAboutToStartEvent event) {
		UpdateRegistriesEvent.EVENT.invoke(new UpdateRegistriesEvent(event.getServer().registryAccess()));
	}
}
