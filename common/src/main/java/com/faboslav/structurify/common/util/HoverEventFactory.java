package com.faboslav.structurify.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

public final class HoverEventFactory
{
	public static HoverEvent createShowText(Component text) {
		//? if <= 1.21.4 {
		/*return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
		*///? } else {
		return new HoverEvent.ShowText(text);
		//?}
	}
}
