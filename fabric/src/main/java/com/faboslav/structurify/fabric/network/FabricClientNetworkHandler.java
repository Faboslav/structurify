package com.faboslav.structurify.fabric.network;

import com.faboslav.structurify.common.network.Packet;
import com.faboslav.structurify.common.network.base.ClientboundPacketType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//? if >= 1.20.2 {
import com.faboslav.structurify.common.network.base.NetworkPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?} else {
/*import net.minecraft.network.FriendlyByteBuf;
*///?}
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
final class FabricClientNetworkHandler
{
	//? if >= 1.20.2 {
	static <T extends Packet<T>> void register(
		CustomPacketPayload.Type<NetworkPacketPayload<T>> payloadType,
		ClientboundPacketType<T> type
	) {
		ClientPlayNetworking.registerGlobalReceiver(
			payloadType,
			(payload, context) -> type.handle(payload.packet()).run()
		);
	}

	static <T extends Packet<T>> void send(Identifier channel, T message) {
		ClientPlayNetworking.send(new NetworkPacketPayload<>(message, channel));
	}
	//?} else {
	/*static <T extends Packet<T>> void register(Identifier id, ClientboundPacketType<T> type) {
		ClientPlayNetworking.registerGlobalReceiver(
			id,
			(client, handler, buf, responseSender) -> {
				T message = type.decode(buf);
				client.execute(() -> type.handle(message).run());
			}
		);
	}

	static void send(Identifier id, FriendlyByteBuf buf) {
		ClientPlayNetworking.send(id, buf);
	}
	*///?}
}
