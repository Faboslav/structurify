package com.faboslav.structurify.fabric.platform;

import com.faboslav.structurify.common.network.base.Network;
import com.faboslav.structurify.common.network.platform.NetworkPlatform;
import com.faboslav.structurify.fabric.network.FabricNetwork;
import net.minecraft.resources.Identifier;

public final class FabricPlatformNetwork implements NetworkPlatform
{
	@Override
	public Network create(Identifier channel, int protocolVersion) {
		return new FabricNetwork(channel, protocolVersion);
	}
}
