package com.faboslav.structurify.common.versions;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;

//? if >= 1.21.11 {
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.permissions.Permission;
//?}

public final class VersionedPermission
{
	//? if >= 1.21.11 {
	public static final Permission PERMISSION_GAMEMASTER = Permissions.COMMANDS_GAMEMASTER;
	public static final Permission PERMISSION_OWNER = Permissions.COMMANDS_OWNER;
	//?} else {
	/*public static final int PERMISSION_GAMEMASTER = 2;
	public static final int PERMISSION_OWNER = 4;
	*///?}

	//? if >= 1.21.11 {
	public static boolean hasPermissions(Player player, Permission permission) {
		return player.permissions().hasPermission(permission);
	}

	public static boolean hasPermissions(CommandSourceStack commandSourceStack, Permission permission) {
		return commandSourceStack.permissions().hasPermission(permission);
	}
	//?} else {
	/*public static boolean hasPermissions(Player player, int permission) {
		return player.hasPermissions(permission);
	}

	public static boolean hasPermissions(CommandSourceStack commandSourceStack, int permission) {
		return commandSourceStack.hasPermission(permission);
	}
	*///?}
}
