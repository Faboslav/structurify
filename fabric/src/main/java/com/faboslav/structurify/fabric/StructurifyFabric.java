package com.faboslav.structurify.fabric;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.lifecycle.DatapackReloadEvent;
import com.faboslav.structurify.common.events.lifecycle.SetupEvent;
import com.faboslav.structurify.common.events.lifecycle.TagsUpdatedEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class StructurifyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
		Structurify.init();

		SetupEvent.EVENT.invoke(new SetupEvent(Runnable::run));

		CommonLifecycleEvents.TAGS_LOADED.register((registryManager, fromPacket) -> TagsUpdatedEvent.EVENT.invoke(new TagsUpdatedEvent(registryManager, fromPacket)));
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
			if(!success) {
				return;
			}

			DatapackReloadEvent.EVENT.invoke(new DatapackReloadEvent(server, serverResourceManager));
		});
    }
}
