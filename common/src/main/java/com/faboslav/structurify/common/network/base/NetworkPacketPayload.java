//? if >= 1.20.2 {
package com.faboslav.structurify.common.network.base;

import com.faboslav.structurify.common.network.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record NetworkPacketPayload<T extends Packet<T>>(
	T packet,
	Type<NetworkPacketPayload<T>> type
) implements CustomPacketPayload
{
	public NetworkPacketPayload(T packet, Identifier channel) {
		this(packet, packet.type().type(channel));
	}
}
//?}
