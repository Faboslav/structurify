package com.faboslav.structurify.common.events.lifecycle;

import com.faboslav.structurify.common.events.base.EventHandler;
import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;


public record DatapackReloadEvent(MinecraftServer server, LifecycledResourceManager serverResourceManager)
{
	public static final EventHandler<DatapackReloadEvent> EVENT = new EventHandler<>();
}
