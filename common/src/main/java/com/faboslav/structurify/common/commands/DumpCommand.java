package com.faboslav.structurify.common.commands;

import com.faboslav.structurify.common.Structurify;
import com.google.common.base.Stopwatch;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument.Result;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.Util;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DumpCommand
{
	private static final int MAX_STRUCTURE_SEARCH_RADIUS = 100;
	private static final DynamicCommandExceptionType ERROR_STRUCTURE_NOT_FOUND = new DynamicCommandExceptionType(
		object -> Component.translatableEscape("commands.locate.structure.not_found", object)
	);
	private static final DynamicCommandExceptionType ERROR_STRUCTURE_INVALID = new DynamicCommandExceptionType(
		object -> Component.translatableEscape("commands.locate.structure.invalid", object)
	);
	private static final DynamicCommandExceptionType ERROR_BIOME_NOT_FOUND = new DynamicCommandExceptionType(
		object -> Component.translatableEscape("commands.locate.biome.not_found", object)
	);
	private static final DynamicCommandExceptionType ERROR_POI_NOT_FOUND = new DynamicCommandExceptionType(
		object -> Component.translatableEscape("commands.locate.poi.not_found", object)
	);

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
									commandContext.getSource(), ResourceOrTagKeyArgument.getResourceOrTagKey(commandContext, "structure", Registries.STRUCTURE, ERROR_STRUCTURE_INVALID)
								)
							)
						)
					)
				)
		);
	}

	private static Optional<? extends HolderSet.ListBacked<Structure>> getHolders(Result<Structure> structure, Registry<Structure> structureRegistry) {
		return structure.unwrap().map(resourceKey -> structureRegistry.get(resourceKey).map(holder -> HolderSet.direct(holder)), structureRegistry::get);
	}

	private static int locateStructure(CommandSourceStack source, Result<Structure> structure) throws CommandSyntaxException {
		ServerLevel serverLevel = source.getLevel();
		BlockPos blockPos = BlockPos.containing(source.getPosition());
		Registry<Structure> registry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);

		HolderSet<Structure> holderSet = (HolderSet<Structure>) getHolders(structure, registry)
			.orElseThrow(() -> ERROR_STRUCTURE_INVALID.create(structure.asPrintable()));

		source.sendSuccess(() -> Component.literal("Locating " + structure.asPrintable() + " in the radius of " + MAX_STRUCTURE_SEARCH_RADIUS + " chunks"), false);

		Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);

		CompletableFuture
			.supplyAsync(() ->
					serverLevel.getChunkSource().getGenerator().findNearestMapStructure(
						serverLevel,
						holderSet,
						blockPos,
						MAX_STRUCTURE_SEARCH_RADIUS,
						false
					),
				Util.backgroundExecutor()
			)
			.thenAcceptAsync(pair -> {
				stopwatch.stop();
				source.getServer().execute(() -> {
					if (pair == null) {
						source.sendFailure(Component.translatable(ERROR_STRUCTURE_NOT_FOUND.create(structure.asPrintable()).getLocalizedMessage()));
					} else {
						LocateCommand.showLocateResult(source, structure, blockPos, pair, "commands.locate.structure.success", false, stopwatch.elapsed());
					}
				});
			}, Util.backgroundExecutor());

		return 0;
	}
}