package com.faboslav.structurify.common.network;

import com.faboslav.structurify.common.network.base.PacketType;

public interface Packet<T extends Packet<T>>
{
	PacketType<T> type();
}
