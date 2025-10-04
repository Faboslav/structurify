package com.faboslav.structurify.common.commands;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.DebugData;
import com.faboslav.structurify.common.mixin.LocateCommandInvoker;
import com.faboslav.structurify.world.level.structure.checks.StructureChecker;
import com.google.common.base.Stopwatch;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument.Result;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.Util;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public final class StructurifyCommand
{
	private static final SuggestionProvider<CommandSourceStack> DEBUG_MODE_SUGGESTIONS =
		(ctx, builder) -> SharedSuggestionProvider.suggest(DebugData.DebugMode.getNames(), builder);


	private static final SuggestionProvider<CommandSourceStack> SAMPLING_MODE_SUGGESTIONS =
		(ctx, builder) -> SharedSuggestionProvider.suggest(DebugData.SamplingMode.getNames(), builder);


	private static final SuggestionProvider<CommandSourceStack> HEIGHTMAP_SUGGESTIONS =
		(ctx, builder) -> SharedSuggestionProvider.suggest(
			Arrays.stream(Heightmap.Types.values()).map(Heightmap.Types::getSerializedName), builder
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
									commandContext.getSource(), ResourceOrTagKeyArgument.getResourceOrTagKey(commandContext, "structure", Registries.STRUCTURE, LocateCommandInvoker.structurify$getStructureInvalidError())
								)
							)
						)
					)
				)
				.then(Commands.literal("debug")
					.then(Commands.literal("enable")
						.requires(src -> src.hasPermission(2))
						.executes(ctx -> {
							Structurify.getConfig().getDebugData().setEnabled(true);
							Structurify.getConfig().getDebugData().setDebugMode(DebugData.DebugMode.FLATNESS);
							Structurify.getConfig().getDebugData().setSamplingMode(DebugData.SamplingMode.FINAL);

							ctx.getSource().sendSuccess(
								() -> Component.literal("Structurify debug enabled."),
								!ctx.getSource().isPlayer()
							);

							return 1;
						})
					)
					.then(Commands.literal("disable")
						.requires(src -> src.hasPermission(2))
						.executes(ctx -> {
							Structurify.getConfig().getDebugData().setEnabled(false);
							Structurify.getConfig().getDebugData().setDebugMode(DebugData.DebugMode.NONE);

							ctx.getSource().sendSuccess(
								() -> Component.literal("Structurify debug disabled."),
								!ctx.getSource().isPlayer()
							);

							return 1;
						})
					)
					.then(Commands.literal("debug_mode")
						.then(Commands.argument("debugMode", StringArgumentType.word())
							.suggests(DEBUG_MODE_SUGGESTIONS)
							.requires(src -> src.hasPermission(2))
							.executes(ctx -> {
								var raw = StringArgumentType.getString(ctx, "debugMode");

								DebugData.DebugMode debugMode;
								try {
									debugMode = DebugData.DebugMode.valueOf(raw.toUpperCase(java.util.Locale.ROOT));
								} catch (IllegalArgumentException ex) {
									ctx.getSource().sendFailure(Component.literal("Unknown debug mode: " + raw));
									return 0;
								}

								Structurify.getConfig().getDebugData().setDebugMode(debugMode);
								reloadStructureChecks(ctx);

								ctx.getSource().sendSuccess(
									() -> Component.literal("Structurify debug mode changed to " + debugMode + "."),
									!ctx.getSource().isPlayer()
								);
								return 1;
							})
						)
					)
					.then(Commands.literal("sampling_mode")
						.then(Commands.argument("samplingMode", StringArgumentType.word())
							.suggests(SAMPLING_MODE_SUGGESTIONS)
							.requires(src -> src.hasPermission(2))
							.executes(ctx -> {
								var raw = StringArgumentType.getString(ctx, "samplingMode");

								DebugData.SamplingMode samplingMode;

								try {
									samplingMode = DebugData.SamplingMode.valueOf(raw.toUpperCase(java.util.Locale.ROOT));
								} catch (IllegalArgumentException ex) {
									ctx.getSource().sendFailure(Component.literal("Unknown sampling mode: " + raw));
									return 0;
								}

								Structurify.getConfig().getDebugData().setSamplingMode(samplingMode);

								Structurify.getConfig().getDebugData().clearStructureFlatnessCheckOverviews();
								Structurify.getConfig().getDebugData().clearStructureFlatnessCheckSamples();
								Structurify.getConfig().getDebugData().clearStructureBiomeCheckOverviews();
								Structurify.getConfig().getDebugData().clearStructureBiomeCheckSamples();

								reloadStructureChecks(ctx);

								ctx.getSource().sendSuccess(
									() -> Component.literal("Structurify debug sampling mode changed to " + samplingMode + "."),
									!ctx.getSource().isPlayer()
								);

								return 1;
							})
						)
					)
					.then(Commands.literal("structures")
						.requires(src -> src.hasPermission(2))
						.executes(ctx -> getStructures(ctx.getSource()))
					)
					.then(Commands.literal("height")
						.then(Commands.argument("heightmap", StringArgumentType.word())
						.suggests(HEIGHTMAP_SUGGESTIONS)
						.requires(src -> src.hasPermission(2))
						.executes(ctx -> {
							var source = ctx.getSource();
							var type = parseHeightmap(StringArgumentType.getString(ctx, "heightmap"));
							return getHeightLevels(source, type);
						})
					)
				)
			)
		);
	}

	private static void reloadStructureChecks(CommandContext<CommandSourceStack> ctx) {
		var serverLevel = ctx.getSource().getLevel();
		var blockPos = BlockPos.containing(ctx.getSource().getPosition());
		var center = new ChunkPos(blockPos);
		var chunkSource = serverLevel.getChunkSource();
		var chunkGenerator = chunkSource.getGenerator();
		var biomeSource = chunkGenerator.getBiomeSource();
		var randomState = chunkSource.randomState();

		int viewDistance = ctx.getSource().getServer().getPlayerList().getViewDistance();
		int chunkRadius = Math.max(1, (int) (viewDistance * 1.33));

		for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
			for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
				ChunkPos chunkPos = new ChunkPos(center.x + dx, center.z + dz);

				var chunk = chunkSource.getChunkNow(chunkPos.x, chunkPos.z);
				if (chunk == null) {
					continue;
				}

				for (var structureStartEntry : chunk.getAllStarts().entrySet()) {
					StructureChecker.debugCheckStructure(structureStartEntry.getValue(), (StructurifyStructure) structureStartEntry.getKey(), chunkGenerator, serverLevel, randomState, biomeSource);
				}
			}
		}
	}

	private static Heightmap.Types parseHeightmap(String s) throws CommandSyntaxException {
		for (var t : Heightmap.Types.values()) {
			if (t.getSerializedName().equalsIgnoreCase(s) || t.name().equalsIgnoreCase(s)) {
				return t;
			}
		}
		throw new SimpleCommandExceptionType(Component.literal("Unknown heightmap: " + s)).create();
	}

	private static int getHeightLevels(CommandSourceStack source, Heightmap.Types type) {
		BlockPos pos = BlockPos.containing(source.getPosition());
		ServerLevel level = source.getLevel();
		ChunkGenerator generator = level.getChunkSource().getGenerator();
		RandomState randomState = level.getChunkSource().randomState();
		LevelHeightAccessor heightView = level;

		int x = pos.getX();
		int z = pos.getZ();

		int occupiedY = generator.getFirstOccupiedHeight(x, z, type, heightView, randomState);
		int freeY = generator.getFirstFreeHeight(x, z, type, heightView, randomState);

		source.sendSuccess(
			() -> Component.literal("Height for " + type.getSerializedName() + "(x: " + x + ", z: " + z + "):" + " - occupied Y: " + occupiedY +  "\n - free Y: " + freeY),
			false
		);

		return 1;
	}

	private static int getStructures(CommandSourceStack source) {
		ServerLevel level = source.getLevel();
		BlockPos pos = BlockPos.containing(source.getPosition());

		return 0;
	}

	private static int locateStructure(CommandSourceStack source, Result<Structure> structure) throws CommandSyntaxException {
		ServerLevel serverLevel = source.getLevel();
		BlockPos blockPos = BlockPos.containing(source.getPosition());
		//? if > 1.21.1 {
		var registry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);
		//?} else {
		/*var registry = source.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE);
		*///?}

		HolderSet<Structure> holderSet = LocateCommandInvoker.structurify$invokeGetHolders(structure, registry)
			.orElseThrow(() -> LocateCommandInvoker.structurify$getStructureInvalidError().create(structure.asPrintable()));

		source.sendSuccess(() -> Component.literal("Locating " + structure.asPrintable() + " in the radius of " + 6400 + " chunks"), false);

		Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);

		CompletableFuture
			.supplyAsync(() ->
					serverLevel.getChunkSource().getGenerator().findNearestMapStructure(
						serverLevel,
						holderSet,
						blockPos,
						6400,
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