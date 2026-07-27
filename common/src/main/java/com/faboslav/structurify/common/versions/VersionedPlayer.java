package com.faboslav.structurify.common.versions;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class VersionedPlayer
{
	public static void sendSystemMessage(Player player, Component message) {
		//? if >= 26.1 {
		player.sendSystemMessage(message);
		//?} else {
		/*player.displayClientMessage(message, false);
		*///?}
	}
}
