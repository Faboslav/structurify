package com.faboslav.structurify.forge.platform;

import com.faboslav.structurify.common.network.base.Network;
import com.faboslav.structurify.common.network.platform.NetworkPlatform;
import com.faboslav.structurify.forge.network.ForgeNetwork;
import net.minecraft.resources.ResourceLocation;

public final class ForgePlatformNetwork implements NetworkPlatform {
    @Override
    public Network create(ResourceLocation channel, int protocolVersion) {
        return new ForgeNetwork(channel, protocolVersion);
    }
}
