package com.faboslav.structurify.neoforge;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.PrepareRegistriesEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
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

		modEventBus.addListener(StructurifyNeoForge::onCommonSetup);

		eventBus.addListener(StructurifyNeoForge::onServerAboutToStart);
		eventBus.addListener(StructurifyNeoForge::onTagsUpdate);
	}

	private static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
		});
	}

	private static void onServerAboutToStart(ServerAboutToStartEvent event) {
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
		PrepareRegistriesEvent.EVENT.invoke(new PrepareRegistriesEvent(event.getServer().registryAccess().freeze()));
	}

	private static void onTagsUpdate(TagsUpdatedEvent event) {
		PrepareRegistriesEvent.EVENT.invoke(new PrepareRegistriesEvent(event.getRegistryAccess().freeze()));
	}
}
