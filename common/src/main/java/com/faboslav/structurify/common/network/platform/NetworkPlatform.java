package com.faboslav.structurify.common.network.platform;

import com.faboslav.structurify.common.network.base.Network;
import net.minecraft.resources.Identifier;

public interface NetworkPlatform
{
	Network create(Identifier channel, int protocolVersion);
}
