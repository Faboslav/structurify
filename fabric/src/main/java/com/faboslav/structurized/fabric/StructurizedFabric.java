package com.faboslav.structurized.fabric;

import com.faboslav.structurized.common.Structurized;
import com.faboslav.structurized.common.events.lifecycle.FinalSetupEvent;
import com.faboslav.structurized.common.events.lifecycle.SetupEvent;
import net.fabricmc.api.ModInitializer;

public final class StructurizedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
		Structurized.init();

		SetupEvent.EVENT.invoke(new SetupEvent(Runnable::run));
		FinalSetupEvent.EVENT.invoke(new FinalSetupEvent(Runnable::run));
    }
}
