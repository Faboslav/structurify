package com.faboslav.structurify.common.network.base;

import com.faboslav.structurify.common.network.Packet;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public interface ServerboundPacketType<T extends Packet<T>> extends PacketType<T>
{
	Consumer<Player> handle(T message);
}
