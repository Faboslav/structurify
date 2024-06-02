package com.faboslav.structurify.fabric;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.lifecycle.DatapackReloadEvent;
import com.faboslav.structurify.common.events.common.PrepareRegistriesEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public final class StructurifyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
		Structurify.init();

		ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
		// CommonLifecycleEvents.TAGS_LOADED.register((registryManager, fromPacket) -> PrepareRegistriesEvent.EVENT.invoke(new PrepareRegistriesEvent(registryManager.toImmutable())));
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
			if(!success) {
				return;
			}

			DatapackReloadEvent.EVENT.invoke(new DatapackReloadEvent(server, serverResourceManager));
		});
    }

	private void onServerStarting(MinecraftServer minecraftServer) {
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
		PrepareRegistriesEvent.EVENT.invoke(new PrepareRegistriesEvent(minecraftServer.getRegistryManager().toImmutable()));
	}
}
