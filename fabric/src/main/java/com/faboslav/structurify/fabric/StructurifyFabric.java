package com.faboslav.structurify.fabric;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

public final class StructurifyFabric implements ModInitializer
{
	@Override
	public void onInitialize() {
		Structurify.init();

		CommonLifecycleEvents.TAGS_LOADED.register(this::onDatapackReload);
		ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
	}

	private void onDatapackReload(RegistryAccess registryAccess, boolean isClient) {
		if(isClient) {
			return;
		}

		StructurifyRegistryManagerProvider.setRegistryManager(registryAccess);
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
	}

	private void onServerStart(MinecraftServer minecraftServer) {
		StructurifyRegistryManagerProvider.setRegistryManager(minecraftServer.registryAccess());
		UpdateRegistriesEvent.EVENT.invoke(new UpdateRegistriesEvent(StructurifyRegistryManagerProvider.getRegistryManager()));
	}
}
