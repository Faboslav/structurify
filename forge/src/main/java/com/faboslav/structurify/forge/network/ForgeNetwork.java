package com.faboslav.structurify.forge.network;

import com.faboslav.structurify.common.network.Packet;
import com.faboslav.structurify.common.network.base.ClientboundPacketType;
import com.faboslav.structurify.common.network.base.Network;
import com.faboslav.structurify.common.network.base.PacketType;
import com.faboslav.structurify.common.network.base.ServerboundPacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ForgeNetwork implements Network {
    private final SimpleChannel channel;
    private int nextId = 0;

    public ForgeNetwork(ResourceLocation channel, int protocolVersion) {
        String version = Integer.toString(protocolVersion);

        this.channel = NetworkRegistry.newSimpleChannel(
                channel,
                () -> version,
                version::equals,
                version::equals
        );
    }

    @Override
    public <T extends Packet<T>> void register(ClientboundPacketType<T> type) {
        this.channel.messageBuilder(type.messageClass(), this.nextId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(type::encode)
                .decoder(type::decode)
                .consumerMainThread((message, context) -> type.handle(message).run())
                .add();
    }

    @Override
    public <T extends Packet<T>> void register(ServerboundPacketType<T> type) {
        this.channel.messageBuilder(type.messageClass(), this.nextId++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(type::encode)
                .decoder(type::decode)
                .consumerMainThread((message, context) -> type.handle(message).accept(context.get().getSender()))
                .add();
    }

    @Override
    public <T extends Packet<T>> void sendToServer(T message) {
        this.channel.sendToServer(message);
    }

    @Override
    public <T extends Packet<T>> void sendToPlayer(T message, ServerPlayer player) {
        this.channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    @Override
    public boolean canSendToPlayer(ServerPlayer player, PacketType<?> type) {
        return this.channel.isRemotePresent(player.connection.connection);
    }
}
