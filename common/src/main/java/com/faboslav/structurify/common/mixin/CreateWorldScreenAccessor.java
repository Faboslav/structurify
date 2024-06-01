package com.faboslav.structurify.common.mixin;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoading;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CreateWorldScreen.class)
public interface CreateWorldScreenAccessor
{
	@Invoker
	static SaveLoading.ServerConfig callCreateServerConfig(
		ResourcePackManager dataPackManager,
		DataConfiguration dataConfiguration
	) {
		throw new UnsupportedOperationException();
	}
}
