package com.faboslav.structurify.common;

import com.faboslav.structurify.common.events.client.ClientLoadedEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class StructurifyClient
{
	public static void init() {
		ClientLoadedEvent.EVENT.addListener(StructurifyClient::loadConfig);
	}

	private static void loadConfig(final ClientLoadedEvent event) {
		if (Structurify.getConfig().isLoaded) {
			return;
		}

		Structurify.getConfig().load();

		List<String> disabledStructures = Structurify.getConfig().getStructureData().entrySet()
			.stream()
			.filter(entry -> entry.getValue().isDisabled())
			.map(Map.Entry::getKey)
			.toList();
		Structurify.getLogger().info("Disabled {} structures: {}", disabledStructures.size(), disabledStructures);
	}
}
