package com.faboslav.structurify.common.util.fabric;

import net.fabricmc.loader.api.FabricLoader;

public final class PlatformImpl {
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    private PlatformImpl() {
    }
}
