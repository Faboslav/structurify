package com.faboslav.structurify.common.events.common;

import com.faboslav.structurify.common.events.base.EventHandler;

public record LoadConfigEvent()
{
	public static final EventHandler<LoadConfigEvent> EVENT = new EventHandler<>();
}