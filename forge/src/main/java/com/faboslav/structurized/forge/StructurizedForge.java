package com.faboslav.structurized.forge;

import com.faboslav.structurized.common.Structurized;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Structurized.MOD_ID)
public final class StructurizedForge {
    public StructurizedForge() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus eventBus = MinecraftForge.EVENT_BUS;

		Structurized.init();
    }
}
