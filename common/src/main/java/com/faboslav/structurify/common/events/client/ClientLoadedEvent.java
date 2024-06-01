package com.faboslav.structurify.common.events.client;

import com.faboslav.structurify.common.events.base.EventHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record ClientLoadedEvent()
{

	public static final EventHandler<ClientLoadedEvent> EVENT = new EventHandler<>();
}