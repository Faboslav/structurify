package com.faboslav.structurify.common.network.base;

import com.faboslav.structurify.common.network.Packet;

public interface ClientboundPacketType<T extends Packet<T>> extends PacketType<T>
{
	Runnable handle(T message);
}
