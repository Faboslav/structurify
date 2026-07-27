package com.faboslav.structurify.common.network.packet;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.StructurifyConfigSerializer;
import com.faboslav.structurify.common.network.MessageHandler;
import com.faboslav.structurify.common.network.Packet;
import com.faboslav.structurify.common.network.base.ClientboundPacketType;
import com.faboslav.structurify.common.network.base.PacketType;
import com.faboslav.structurify.common.versions.VersionedPlayer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
//? if >= 1.20.2 {
import net.minecraft.network.RegistryFriendlyByteBuf;
//?} else {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public record ConfigSyncToClientPacket(String config, boolean save) implements Packet<ConfigSyncToClientPacket>
{
	private static final Gson GSON = new Gson();
	public static final Identifier ID = Structurify.makeId("config_sync_to_client_packet");
	public static final ClientboundPacketType<ConfigSyncToClientPacket> TYPE = new Handler();

	public static void sendToClient(StructurifyConfig config, Player player, boolean save) {
		MessageHandler.DEFAULT_CHANNEL.sendToPlayer(
			new ConfigSyncToClientPacket(
				GSON.toJson(StructurifyConfigSerializer.save(config)),
				save
			),
			player
		);
	}

	@Override
	public PacketType<ConfigSyncToClientPacket> type() {
		return TYPE;
	}

	public static class Handler implements ClientboundPacketType<ConfigSyncToClientPacket>
	{
		@Override
		public Identifier id() {
			return ID;
		}

		@Override
		public Runnable handle(final ConfigSyncToClientPacket packet) {
			return () -> {
				Player player = Minecraft.getInstance().player;

				try {
					StructurifyConfigSerializer.load(Structurify.getConfig(), GSON.fromJson(packet.config(), JsonObject.class));

					if (packet.save()) {
						Structurify.getConfig().save();
					}
				} catch (Throwable e) {
					Structurify.getLogger().error("Failed to load config from server.", e);

					if (player != null) {
						VersionedPlayer.sendSystemMessage(player, Component.literal("Failed to sync the Structurify config from the server."));
					}

					return;
				}

				if (packet.save() && player != null) {
					VersionedPlayer.sendSystemMessage(player, Component.literal("Structurify config synced from the server."));
				}
			};
		}

		//? if >= 1.20.2 {
		public ConfigSyncToClientPacket decode(final RegistryFriendlyByteBuf buf) {
			return new ConfigSyncToClientPacket(buf.readUtf(), buf.readBoolean());
		}

		public void encode(final ConfigSyncToClientPacket packet, final RegistryFriendlyByteBuf buf) {
			buf.writeUtf(packet.config());
			buf.writeBoolean(packet.save());
		}
		//?} else {
		/*public ConfigSyncToClientPacket decode(final FriendlyByteBuf buf) {
			return new ConfigSyncToClientPacket(buf.readUtf(), buf.readBoolean());
		}

		public void encode(final ConfigSyncToClientPacket packet, final FriendlyByteBuf buf) {
			buf.writeUtf(packet.config());
			buf.writeBoolean(packet.save());
		}

		@Override
		public Class<ConfigSyncToClientPacket> messageClass() {
			return ConfigSyncToClientPacket.class;
		}
		*///?}
	}
}