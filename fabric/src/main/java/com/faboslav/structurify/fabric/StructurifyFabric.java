package com.faboslav.structurify.fabric;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.PrepareRegistriesEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;

public final class StructurifyFabric implements ModInitializer
{
	@Override
	public void onInitialize() {
		Structurify.init();

		ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(this::onDatapackReload);
	}

	private void onServerStarting(MinecraftServer minecraftServer) {
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
		PrepareRegistriesEvent.EVENT.invoke(new PrepareRegistriesEvent(minecraftServer.registryAccess().freeze()));
	}

	private void onDatapackReload(
		MinecraftServer minecraftServer,
		CloseableResourceManager serverResourceManager,
		boolean success
	) {
		if (!success) {
			return;
		}

		PrepareRegistriesEvent.EVENT.invoke(new PrepareRegistriesEvent(minecraftServer.registryAccess().freeze()));
	}
}
