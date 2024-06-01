package com.faboslav.structurify.fabric;

import com.faboslav.structurify.common.StructurifyClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class StructurifyFabricClient implements ClientModInitializer
{
	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		StructurifyClient.init();
	}
}

