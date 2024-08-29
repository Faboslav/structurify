package com.faboslav.structurify.common.events.lifecycle;

import com.faboslav.structurify.common.events.base.EventHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;

public record DatapackReloadEvent(MinecraftServer server, CloseableResourceManager serverResourceManager)
{
	public static final EventHandler<DatapackReloadEvent> EVENT = new EventHandler<>();
}
