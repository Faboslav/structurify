package com.faboslav.structurify.common.commands;

import com.faboslav.structurify.common.Structurify;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class DumpCommand {
	public static void createCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			Commands.literal("structurify")
				.requires(source -> source.hasPermission(2))
				.then(Commands.literal("dump")
					.executes(context -> {
						Structurify.getConfig().dump();
						context.getSource().sendSuccess(
							() -> Component.literal("Structurify config dumped to \"" + Structurify.getConfig().configDumpPath + "\"."),
							!context.getSource().isPlayer()
						);
						return 1;
					})
				)
		);
	}
}