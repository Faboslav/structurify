package com.faboslav.structurify.common.config.gui;

import com.faboslav.structurify.common.events.client.ClientLoadedEvent;
import dev.isxander.yacl3.api.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public final class StructureConfigScreen
{
	public static Screen create(Screen parent) {
		ClientLoadedEvent.EVENT.invoke(new ClientLoadedEvent());

		var yacl = YetAnotherConfigLib.createBuilder()
			.title(Text.translatable("structurized.name"));

		return yacl.build().generateScreen(parent);
	}
}