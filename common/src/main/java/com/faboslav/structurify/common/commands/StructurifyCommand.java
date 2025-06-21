package com.faboslav.structurify.common.commands;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.mixin.LocateCommandInvoker;
import com.google.common.base.Stopwatch;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument.Result;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.Util;

import java.util.concurrent.CompletableFuture;

public final class StructurifyCommand
{
	public static void createCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		dispatcher.register(
			Commands.literal("structurify")
				.requires(source -> source.hasPermission(2))
				.then(Commands.literal("dump")
					.executes(ctx -> {
						Structurify.getConfig().dump();
						ctx.getSource().sendSuccess(
							() -> Component.literal("Structurify config dumped to \"" + Structurify.getConfig().configDumpPath + "\"."),
							!ctx.getSource().isPlayer()
						);
						return 1;
					})
				)
				.then(Commands.literal("locate")
					.then(Commands.literal("structure")
						.then(Commands.argument("structure", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.STRUCTURE))
							.executes(
								commandContext -> locateStructure(
									commandContext.getSource(), ResourceOrTagKeyArgument.getResourceOrTagKey(commandContext, "structure", Registries.STRUCTURE, LocateCommandInvoker.structurify$getStructureInvalidError())
								)
							)
						)
					)
				)
		);
	}

	private static int locateStructure(CommandSourceStack source, Result<Structure> structure) throws CommandSyntaxException {
		ServerLevel serverLevel = source.getLevel();
		BlockPos blockPos = BlockPos.containing(source.getPosition());
		//? > 1.21.1 {
		var registry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);
		//?} else {
		/*var registry = source.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE);
		*///?}

		HolderSet<Structure> holderSet = LocateCommandInvoker.structurify$invokeGetHolders(structure, registry)
			.orElseThrow(() -> LocateCommandInvoker.structurify$getStructureInvalidError().create(structure.asPrintable()));

		source.sendSuccess(() -> Component.literal("Locating " + structure.asPrintable() + " in the radius of " + LocateCommandInvoker.structurify$getMaxStructureSearchRadius() + " chunks"), false);

		Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);

		CompletableFuture
			.supplyAsync(() ->
					serverLevel.getChunkSource().getGenerator().findNearestMapStructure(
						serverLevel,
						holderSet,
						blockPos,
						LocateCommandInvoker.structurify$getMaxStructureSearchRadius(),
						false
					),
				Util.backgroundExecutor()
			)
			.thenAcceptAsync(pair -> {
				stopwatch.stop();
				source.getServer().execute(() -> {
					if (pair == null) {
						source.sendFailure(Component.translatable(LocateCommandInvoker.structurify$getStructureNotFoundError().create(structure.asPrintable()).getLocalizedMessage()));
					} else {
						LocateCommand.showLocateResult(source, structure, blockPos, pair, "commands.locate.structure.success", false, stopwatch.elapsed());
					}
				});
			}, Util.backgroundExecutor());

		return 0;
	}
}