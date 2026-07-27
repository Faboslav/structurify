package com.faboslav.structurify.common.network.packet;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.StructurifyConfig;
import com.faboslav.structurify.common.config.StructurifyConfigSerializer;
import com.faboslav.structurify.common.network.MessageHandler;
import com.faboslav.structurify.common.network.Packet;
import com.faboslav.structurify.common.network.base.ClientboundPacketType;
import com.faboslav.structurify.common.network.base.PacketType;
import com.faboslav.structurify.common.platform.PlatformHooks;
import com.faboslav.structurify.common.versions.VersionedPlayer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
//? if >= 1.20.2 {
import net.minecraft.network.RegistryFriendlyByteBuf;
 //?} else {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public record ConfigStatusToClientPacket(String config) implements Packet<ConfigStatusToClientPacket>
{
	private static final Gson GSON = new Gson();
	public static final Identifier ID = Structurify.makeId("config_status_to_client_packet");
	public static final ClientboundPacketType<ConfigStatusToClientPacket> TYPE = new Handler();

	public static void sendToClient(StructurifyConfig config, Player player) {
		MessageHandler.DEFAULT_CHANNEL.sendToPlayer(
			new ConfigStatusToClientPacket(
				GSON.toJson(StructurifyConfigSerializer.save(config))
			),
			player
		);
	}

	@Override
	public PacketType<ConfigStatusToClientPacket> type() {
		return TYPE;
	}

	public static class Handler implements ClientboundPacketType<ConfigStatusToClientPacket>
	{
		@Override
		public Identifier id() {
			return ID;
		}

		@Override
		public Runnable handle(final ConfigStatusToClientPacket packet) {
			return () -> {
				JsonObject serverConfigJson;
				Player player = Minecraft.getInstance().player;

				try {
					serverConfigJson = GSON.fromJson(packet.config(), JsonObject.class);
				} catch (Throwable e) {
					Structurify.getLogger().error("Failed to read config status from server.", e);

					if (player != null) {
						VersionedPlayer.sendSystemMessage(player, Component.literal("Failed to check the Structurify config status."));
					}

					return;
				}

				if (player != null) {
					VersionedPlayer.sendSystemMessage(player, describeConfigStatus(Structurify.getConfig(), serverConfigJson));
				}
			};
		}

		//? if >= 1.20.2 {
		public ConfigStatusToClientPacket decode(final RegistryFriendlyByteBuf buf) {
			return new ConfigStatusToClientPacket(buf.readUtf());
		}

		public void encode(final ConfigStatusToClientPacket packet, final RegistryFriendlyByteBuf buf) {
			buf.writeUtf(packet.config());
		}
		//?} else {
		/*public ConfigStatusToClientPacket decode(final FriendlyByteBuf buf) {
			return new ConfigStatusToClientPacket(buf.readUtf());
		}

		public void encode(final ConfigStatusToClientPacket packet, final FriendlyByteBuf buf) {
			buf.writeUtf(packet.config());
		}

		@Override
		public Class<ConfigStatusToClientPacket> messageClass() {
			return ConfigStatusToClientPacket.class;
		}
		*///?}
	}

	private static Component describeConfigStatus(
		StructurifyConfig localConfig,
		JsonObject serverConfigJson
	) {
		String localHash = StructurifyConfigSerializer.computeConfigHash(localConfig);
		String serverHash = StructurifyConfigSerializer.hashConfigJson(serverConfigJson);

		String localVersion = PlatformHooks.PLATFORM_HELPER.getModVersion();
		String serverVersion = serverConfigJson.has(
			StructurifyConfigSerializer.CONFIG_VERSION_PROPERTY
		)
			? serverConfigJson.get(
			StructurifyConfigSerializer.CONFIG_VERSION_PROPERTY
		).getAsString()
			:"unknown";

		boolean isSynchronized = localHash.equals(serverHash);

		MutableComponent message = isSynchronized
			? Component.literal("Structurify config is synchronized with the server.")
			.withStyle(ChatFormatting.GREEN)
			:Component.literal("Structurify config differs from the server.")
			.withStyle(ChatFormatting.RED);

		message.append(
			Component.literal("\nLocal version: " + localVersion)
				.withStyle(ChatFormatting.GRAY)
		);

		message.append(
			Component.literal("\nServer version: " + serverVersion)
				.withStyle(ChatFormatting.GRAY)
		);

		return message;
	}
}