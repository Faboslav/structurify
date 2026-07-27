package com.faboslav.structurify.common.network.packet;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.StructurifyConfigSerializer;
import com.faboslav.structurify.common.network.MessageHandler;
import com.faboslav.structurify.common.network.Packet;
import com.faboslav.structurify.common.network.base.PacketType;
import com.faboslav.structurify.common.network.base.ServerboundPacketType;
import com.faboslav.structurify.common.versions.VersionedPlayer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
//? if >= 1.20.2 {
import net.minecraft.network.RegistryFriendlyByteBuf;
 //?} else {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public record ConfigSyncToServerPacket(String config) implements Packet<ConfigSyncToServerPacket>
{
	private static final Gson GSON = new Gson();
	public static final Identifier ID = Structurify.makeId("config_sync_to_server_packet");
	public static final ServerboundPacketType<ConfigSyncToServerPacket> TYPE = new Handler();

	public static void sendToServer(StructurifyConfig config) {
		MessageHandler.DEFAULT_CHANNEL.sendToServer(new ConfigSyncToServerPacket(GSON.toJson(StructurifyConfigSerializer.save(config))));
	}

	@Override
	public PacketType<ConfigSyncToServerPacket> type() {
		return TYPE;
	}

	public static class Handler implements ServerboundPacketType<ConfigSyncToServerPacket>
	{
		@Override
		public Identifier id() {
			return ID;
		}

		@Override
		public Consumer<Player> handle(final ConfigSyncToServerPacket packet) {
			return (player) -> {
				try {
					StructurifyConfigSerializer.load(Structurify.getConfig(), GSON.fromJson(packet.config(), JsonObject.class));
					Structurify.getConfig().save();
				} catch (Throwable e) {
					Structurify.getLogger().error("Failed to load config to server.", e);
					VersionedPlayer.sendSystemMessage(player, Component.literal("Failed to sync the Structurify config to the server."));
					return;
				}

				VersionedPlayer.sendSystemMessage(player, Component.literal("Structurify config synced to the server."));

				if (player instanceof ServerPlayer serverPlayer) {
					MessageHandler.DEFAULT_CHANNEL.sendToAllPlayers(
						new ConfigSyncToClientPacket(
							GSON.toJson(StructurifyConfigSerializer.save(Structurify.getConfig())),
							false
						),
						serverPlayer.level().getServer()
					);
				}
			};
		}

		//? if >= 1.20.2 {
		public ConfigSyncToServerPacket decode(final RegistryFriendlyByteBuf buf) {
			return new ConfigSyncToServerPacket(buf.readUtf());
		}

		public void encode(final ConfigSyncToServerPacket packet, final RegistryFriendlyByteBuf buf) {
			buf.writeUtf(packet.config());
		}
		//?} else {
		/*public ConfigSyncToServerPacket decode(final FriendlyByteBuf buf) {
			return new ConfigSyncToServerPacket(buf.readUtf());
		}

		public void encode(final ConfigSyncToServerPacket packet, final FriendlyByteBuf buf) {
			buf.writeUtf(packet.config());
		}

		@Override
		public Class<ConfigSyncToServerPacket> messageClass() {
			return ConfigSyncToServerPacket.class;
		}
		*///?}
	}
}