package com.faboslav.structurify.neoforge.network;

import com.faboslav.structurify.common.network.Packet;
import com.faboslav.structurify.common.network.base.*;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

//? if >= 1.21.8 {
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
//?}

public final class NeoForgeNetwork implements Network
{
	//? if >= 1.21.10 {
	private static final boolean IS_CLIENT = FMLLoader.getCurrent().getDist().isClient();
	//?} else if >= 1.21.8 {
	/*private static final boolean IS_CLIENT = FMLLoader.getDist().isClient();
	*///?}

	private final List<ClientboundPacketType<?>> clientPackets = new ArrayList<>();
	private final List<ServerboundPacketType<?>> serverPackets = new ArrayList<>();
	private final Identifier channel;
	private final String version;

	public NeoForgeNetwork(Identifier channel, int protocolVersion) {
		this.channel = channel.withSuffix("/v" + protocolVersion);
		this.version = "v" + protocolVersion;
	}

	@Override
	public <T extends Packet<T>> void register(ClientboundPacketType<T> type) {
		this.clientPackets.add(type);
	}

	@Override
	public <T extends Packet<T>> void register(ServerboundPacketType<T> type) {
		this.serverPackets.add(type);
	}

	@Override
	public <T extends Packet<T>> void sendToServer(T message) {
		//? if >= 1.21.8 {
		if (!IS_CLIENT) {
			return;
		}

		ClientPacketDistributor.sendToServer(new NetworkPacketPayload<>(message, this.channel));
		//?} else {
		/*PacketDistributor.sendToServer(new NetworkPacketPayload<>(message, this.channel));
		*///?}
	}

	@Override
	public <T extends Packet<T>> void sendToPlayer(T message, ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, new NetworkPacketPayload<>(message, this.channel));
	}

	@Override
	public boolean canSendToPlayer(ServerPlayer player, PacketType<?> type) {
		return player.connection.hasChannel(type.type(this.channel));
	}

	public void onNetworkSetup(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(this.version);

		for (ClientboundPacketType<?> type : this.clientPackets) {
			this.registerClientbound(registrar, type);
		}

		for (ServerboundPacketType<?> type : this.serverPackets) {
			this.registerServerbound(registrar, type);
		}
	}

	private <T extends Packet<T>> void registerClientbound(PayloadRegistrar registrar, ClientboundPacketType<T> type) {
		CustomPacketPayload.Type<NetworkPacketPayload<T>> payloadType = type.type(this.channel);
		registrar.playToClient(payloadType, type.codec(payloadType), (payload, context) -> type.handle(payload.packet()).run());
	}

	private <T extends Packet<T>> void registerServerbound(PayloadRegistrar registrar, ServerboundPacketType<T> type) {
		CustomPacketPayload.Type<NetworkPacketPayload<T>> payloadType = type.type(this.channel);
		registrar.playToServer(payloadType, type.codec(payloadType), (payload, context) -> type.handle(payload.packet()).accept(context.player()));
	}
}
