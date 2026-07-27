package com.faboslav.structurify.common.network;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.network.packet.ConfigStatusToClientPacket;
import com.faboslav.structurify.common.network.packet.ConfigSyncRequestToClientPacket;
import com.faboslav.structurify.common.network.packet.ConfigSyncToClientPacket;
import com.faboslav.structurify.common.network.packet.ConfigSyncToServerPacket;

public final class MessageHandler
{
	public static final NetworkChannel DEFAULT_CHANNEL = new NetworkChannel(Structurify.makeId("network"), 1);

	public static void init() {
		DEFAULT_CHANNEL.register(ConfigStatusToClientPacket.TYPE);
		DEFAULT_CHANNEL.register(ConfigSyncRequestToClientPacket.TYPE);
		DEFAULT_CHANNEL.register(ConfigSyncToClientPacket.TYPE);
		DEFAULT_CHANNEL.register(ConfigSyncToServerPacket.TYPE);
	}
}
