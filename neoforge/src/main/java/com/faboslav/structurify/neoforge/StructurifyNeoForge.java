package com.faboslav.structurify.neoforge;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.commands.StructurifyCommand;
import com.faboslav.structurify.common.events.common.LoadConfigEvent;
import com.faboslav.structurify.common.events.common.UpdateRegistriesEvent;
import com.faboslav.structurify.common.registry.StructurifyRegistryManagerProvider;
import com.faboslav.structurify.neoforge.platform.NeoForgePlatformNetwork;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(Structurify.MOD_ID)
public final class StructurifyNeoForge
{
	public StructurifyNeoForge(ModContainer modContainer, IEventBus modEventBus) {
		var eventBus = NeoForge.EVENT_BUS;

		Structurify.init();

		//? if >= 1.21.9 {
		if (FMLEnvironment.getDist() == Dist.CLIENT)
		//?} else {
		/*if (FMLEnvironment.dist == Dist.CLIENT)
		*///?}
		{
			StructurifyNeoForgeClient.init(modEventBus, eventBus);
		}

		modEventBus.addListener(StructurifyNeoForge::onRegisterPayloadHandlers);

		eventBus.addListener(StructurifyNeoForge::registerCommand);
		//? if >= 26.2 {
		eventBus.addListener(EventPriority.LOWEST, StructurifyNeoForge::onServerDataLoad);
		//?} else {
		/*eventBus.addListener(EventPriority.LOWEST, StructurifyNeoForge::onResourceManagerReload);
		*///?}
		eventBus.addListener(EventPriority.LOWEST, StructurifyNeoForge::onServerAboutToStart);
	}

	private static void onRegisterPayloadHandlers(final RegisterPayloadHandlersEvent event) {
		NeoForgePlatformNetwork.onRegisterPayloadHandlers(event);
	}

	private static void registerCommand(final RegisterCommandsEvent event) {
		StructurifyCommand.createCommand(event.getDispatcher(), event.getBuildContext());
	}

	//? if >= 26.2 {
	private static void onServerDataLoad(final TagsUpdatedEvent.ServerDataLoad event) {
		StructurifyRegistryManagerProvider.setRegistryManager(event.getRegistries());
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
	}
	//?} else {
	/*private static void onResourceManagerReload(final TagsUpdatedEvent event) {
		if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
			return;
		}

		//? if >=1.21.3 {
		var registryAccess = event.getLookupProvider();
		//?} else {
		/^var registryAccess = event.getRegistryAccess();
		 ^///?}

		StructurifyRegistryManagerProvider.setRegistryManager(registryAccess);
		LoadConfigEvent.EVENT.invoke(new LoadConfigEvent());
	}
	*///?}

	private static void onServerAboutToStart(final ServerAboutToStartEvent event) {
		StructurifyRegistryManagerProvider.setRegistryManager(event.getServer().registryAccess());
		UpdateRegistriesEvent.EVENT.invoke(new UpdateRegistriesEvent(event.getServer().registryAccess()));
	}
}
