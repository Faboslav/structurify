package com.faboslav.structurify.quilt;

import com.faboslav.structurify.common.Structurify;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public final class StructurizedQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        Structurify.init();
    }
}
