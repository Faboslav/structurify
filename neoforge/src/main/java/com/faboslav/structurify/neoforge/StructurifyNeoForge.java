package com.faboslav.structurify.neoforge;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.commands.DumpCommand;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
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

		eventBus.addListener(StructurifyNeoForge::registerCommand);
		eventBus.addListener(StructurifyNeoForge::onResourceManagerReload);
		eventBus.addListener(StructurifyNeoForge::onServerAboutToStart);
	}

	private static void registerCommand(RegisterCommandsEvent event) {
		DumpCommand.createCommand(event.getDispatcher());
	}

	private static void onResourceManagerReload(TagsUpdatedEvent event) {
		if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
			return;
		}

		//? >=1.21.3 {
		var registryAccess = event.getLookupProvider();
		//?} else {
		/*var registryAccess = event.getRegistryAccess();
		 *///?}

		StructurifyRegistryManagerProvider.setRegistryManager(registryAccess);
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
	}

	private static void onServerAboutToStart(ServerAboutToStartEvent event) {
		StructurifyRegistryManagerProvider.setRegistryManager(event.getServer().registryAccess());
		UpdateRegistriesEvent.EVENT.invoke(new UpdateRegistriesEvent(event.getServer().registryAccess()));
	}
}
