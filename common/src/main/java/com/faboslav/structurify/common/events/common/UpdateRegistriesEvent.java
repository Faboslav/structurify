package com.faboslav.structurify.common.events.common;

import com.faboslav.structurify.common.events.base.EventHandler;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;

public record UpdateRegistriesEvent(HolderLookup.Provider registryManager)
{
	public static final EventHandler<UpdateRegistriesEvent> EVENT = new EventHandler<>();
}
