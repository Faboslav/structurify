package com.faboslav.structurify.fabric.network;

import com.faboslav.structurify.common.network.Packet;
import com.faboslav.structurify.common.network.base.*;
import net.fabricmc.api.EnvType;
//? if >= 1.20.2 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else {
/*import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
*///?}
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public final class FabricNetwork implements Network
{
	private static final boolean IS_CLIENT = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

	private final Identifier channel;

	public FabricNetwork(Identifier channel, int protocolVersion) {
		this.channel = channel.withSuffix("/v" + protocolVersion);
	}

	//? if >= 1.20.2 {
	@Override
	public <T extends Packet<T>> void register(ClientboundPacketType<T> type) {
		CustomPacketPayload.Type<NetworkPacketPayload<T>> payloadType = type.type(this.channel);
		//? if >= 26.1 {
		PayloadTypeRegistry.clientboundPlay().register(payloadType, type.codec(payloadType));
		//?} else {
		/*PayloadTypeRegistry.playS2C().register(payloadType, type.codec(payloadType));
		*///?}

		if (!IS_CLIENT) {
			return;
		}

		FabricClientNetworkHandler.register(payloadType, type);
	}

	@Override
	public <T extends Packet<T>> void register(ServerboundPacketType<T> type) {
		CustomPacketPayload.Type<NetworkPacketPayload<T>> payloadType = type.type(this.channel);
		//? if >= 26.1 {
		PayloadTypeRegistry.serverboundPlay().register(payloadType, type.codec(payloadType));
		//?} else {
		/*PayloadTypeRegistry.playC2S().register(payloadType, type.codec(payloadType));
		*///?}

		ServerPlayNetworking.registerGlobalReceiver(
			payloadType,
			(payload, context) -> type.handle(payload.packet()).accept(context.player())
		);
	}

	@Override
	public <T extends Packet<T>> void sendToServer(T message) {
		if (!IS_CLIENT) {
			return;
		}

		FabricClientNetworkHandler.send(this.channel, message);
	}

	@Override
	public <T extends Packet<T>> void sendToPlayer(T message, ServerPlayer player) {
		ServerPlayNetworking.send(player, new NetworkPacketPayload<>(message, this.channel));
	}

	@Override
	public boolean canSendToPlayer(ServerPlayer player, PacketType<?> type) {
		return ServerPlayNetworking.canSend(player, type.type(this.channel));
	}
	//?} else {
	/*private Identifier packetId(PacketType<?> type) {
		return this.channel.withSuffix("/" + type.id().getPath());
	}

	private <T extends Packet<T>> FriendlyByteBuf encode(T message) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		message.type().encode(message, buf);
		return buf;
	}

	@Override
	public <T extends Packet<T>> void register(ClientboundPacketType<T> type) {
		if (!IS_CLIENT) {
			return;
		}

		FabricClientNetworkHandler.register(packetId(type), type);
	}

	@Override
	public <T extends Packet<T>> void register(ServerboundPacketType<T> type) {
		ServerPlayNetworking.registerGlobalReceiver(
			packetId(type),
			(server, player, handler, buf, responseSender) -> {
				T message = type.decode(buf);
				server.execute(() -> type.handle(message).accept(player));
			}
		);
	}

	@Override
	public <T extends Packet<T>> void sendToServer(T message) {
		if (!IS_CLIENT) {
			return;
		}

		FabricClientNetworkHandler.send(packetId(message.type()), encode(message));
	}

	@Override
	public <T extends Packet<T>> void sendToPlayer(T message, ServerPlayer player) {
		ServerPlayNetworking.send(player, packetId(message.type()), encode(message));
	}

	@Override
	public boolean canSendToPlayer(ServerPlayer player, PacketType<?> type) {
		return ServerPlayNetworking.canSend(player, packetId(type));
	}
	*///?}
}
