package com.faboslav.structurify.common.util;

import net.minecraft.network.chat.ClickEvent;

public final class ClickEventFactory
{
	public static ClickEvent createRunCommand(String command) {
		//? if <= 1.21.4 {
		/*return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
		*///? } else {
		return new ClickEvent.RunCommand(command);
		//?}
	}

	public static ClickEvent createSuggestCommand(String command) {
		//? if <= 1.21.4 {
		/*return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
		*///? } else {
		return new ClickEvent.SuggestCommand(command);
		//?}
	}
}
