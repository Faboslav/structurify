package com.faboslav.structurify.common.network;

import com.faboslav.structurify.common.network.base.ClientboundPacketType;
import com.faboslav.structurify.common.network.base.Network;
import com.faboslav.structurify.common.network.base.PacketType;
import com.faboslav.structurify.common.network.base.ServerboundPacketType;
import com.faboslav.structurify.common.platform.PlatformHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public final class NetworkChannel implements Network
{
	private final Network network;

	public NetworkChannel(Identifier channel, int protocolVersion) {
		this.network = PlatformHooks.NETWORK_PLATFORM.create(channel, protocolVersion);
	}

	@Override
	public <T extends Packet<T>> void register(ClientboundPacketType<T> type) {
		this.network.register(type);
	}

	@Override
	public <T extends Packet<T>> void register(ServerboundPacketType<T> type) {
		this.network.register(type);
	}

	@Override
	public <T extends Packet<T>> void sendToServer(T message) {
		this.network.sendToServer(message);
	}

	@Override
	public <T extends Packet<T>> void sendToPlayer(T message, ServerPlayer player) {
		this.network.sendToPlayer(message, player);
	}

	public <T extends Packet<T>> void sendToPlayer(T message, Player player) {
		if (player instanceof ServerPlayer serverPlayer) {
			sendToPlayer(message, serverPlayer);
		}
	}

	public <T extends Packet<T>> void sendToAllPlayers(T message, MinecraftServer server) {
		server.getPlayerList().getPlayers().forEach(player -> sendToPlayer(message, player));
	}

	public <T extends Packet<T>> void sendToAllLoaded(T message, Level level, BlockPos pos) {
		LevelChunk chunk = level.getChunkAt(pos);
		if (level.getChunkSource() instanceof ServerChunkCache serverChunkCache) {
			serverChunkCache.chunkMap.getPlayers(chunk.getPos(), false).forEach(player -> sendToPlayer(message, player));
		}
	}

	@Override
	public boolean canSendToPlayer(ServerPlayer player, PacketType<?> type) {
		return this.network.canSendToPlayer(player, type);
	}
}
