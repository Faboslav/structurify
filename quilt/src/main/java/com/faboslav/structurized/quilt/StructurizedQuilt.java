package com.faboslav.structurized.quilt;

import com.faboslav.structurized.common.Structurized;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public final class StructurizedQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        Structurized.init();
    }
}
