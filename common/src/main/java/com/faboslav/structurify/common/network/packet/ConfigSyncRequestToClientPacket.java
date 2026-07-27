package com.faboslav.structurify.common.network.packet;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.network.MessageHandler;
import com.faboslav.structurify.common.network.Packet;
import com.faboslav.structurify.common.network.base.ClientboundPacketType;
import com.faboslav.structurify.common.network.base.PacketType;
//? if >= 1.20.2 {
import net.minecraft.network.RegistryFriendlyByteBuf;
 //?} else {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public record ConfigSyncRequestToClientPacket() implements Packet<ConfigSyncRequestToClientPacket>
{
	public static final Identifier ID = Structurify.makeId("config_sync_request_to_client_packet");
	public static final ClientboundPacketType<ConfigSyncRequestToClientPacket> TYPE = new Handler();

	public static void sendToClient(Player player) {
		MessageHandler.DEFAULT_CHANNEL.sendToPlayer(new ConfigSyncRequestToClientPacket(), player);
	}

	@Override
	public PacketType<ConfigSyncRequestToClientPacket> type() {
		return TYPE;
	}

	public static class Handler implements ClientboundPacketType<ConfigSyncRequestToClientPacket>
	{
		@Override
		public Identifier id() {
			return ID;
		}

		@Override
		public Runnable handle(final ConfigSyncRequestToClientPacket packet) {
			return () -> ConfigSyncToServerPacket.sendToServer(Structurify.getConfig());
		}

		//? if >= 1.20.2 {
		public ConfigSyncRequestToClientPacket decode(final RegistryFriendlyByteBuf buf) {
			return new ConfigSyncRequestToClientPacket();
		}

		public void encode(final ConfigSyncRequestToClientPacket packet, final RegistryFriendlyByteBuf buf) {
		}
		//?} else {
		/*public ConfigSyncRequestToClientPacket decode(final FriendlyByteBuf buf) {
			return new ConfigSyncRequestToClientPacket();
		}

		public void encode(final ConfigSyncRequestToClientPacket packet, final FriendlyByteBuf buf) {
		}

		@Override
		public Class<ConfigSyncRequestToClientPacket> messageClass() {
			return ConfigSyncRequestToClientPacket.class;
		}
		*///?}
	}
}