package com.faboslav.structurify.common.commands;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.api.StructurifyStructure;
import com.faboslav.structurify.common.config.data.DebugData;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.mixin.LocateCommandInvoker;
import com.faboslav.structurify.common.mixin.ResourceKeyArgumentInvoker;
import com.faboslav.structurify.common.util.ChunkPosUtil;
import com.faboslav.structurify.common.util.ClickEventFactory;
import com.faboslav.structurify.common.util.HoverEventFactory;
import com.faboslav.structurify.common.world.level.structure.checks.StructureChecker;
import com.google.common.base.Stopwatch;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument.Result;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import java.util.concurrent.CompletableFuture;

//? if >= 1.21.11 {
import net.minecraft.server.permissions.Permissions;
//?}

public final class StructurifyCommand
{
	private static final SuggestionProvider<CommandSourceStack> DEBUG_MODE_SUGGESTIONS =
		(ctx, builder) -> SharedSuggestionProvider.suggest(DebugData.DebugMode.getNames(), builder);


	private static final SuggestionProvider<CommandSourceStack> SAMPLING_MODE_SUGGESTIONS =
		(ctx, builder) -> SharedSuggestionProvider.suggest(DebugData.SamplingMode.getNames(), builder);

	public static void createCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		dispatcher.register(
			Commands.literal("structurify")
				//? if >= 1.21.11 {
				.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
				//?} else {
				/*.requires(source -> source.hasPermission(2))
				 *///?}
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
				.then(Commands.literal("structure")
					//? if >= 1.21.11 {
					.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
					//?} else {
					/*.requires(source -> source.hasPermission(2))
					 *///?}
					.then(Commands.literal("list")
						.executes(ctx -> getStructureList(ctx.getSource())))
					.then(Commands.literal("enable")
						.then(Commands.argument("structure", ResourceKeyArgument.key(Registries.STRUCTURE))
							.executes(
								commandContext -> changeStructure(
									commandContext.getSource(), ResourceKeyArgumentInvoker.structurify$invokegetRegistryKey(commandContext, "structure", Registries.STRUCTURE, LocateCommandInvoker.structurify$getStructureInvalidError()), true
								)
							)
						)
					)
					.then(Commands.literal("disable")
						.then(Commands.argument("structure", ResourceKeyArgument.key(Registries.STRUCTURE))
							.executes(
								commandContext -> changeStructure(
									commandContext.getSource(), ResourceKeyArgumentInvoker.structurify$invokegetRegistryKey(commandContext, "structure", Registries.STRUCTURE, LocateCommandInvoker.structurify$getStructureInvalidError()), true
								)
							)
						)
					)
				)
				.then(Commands.literal("debug")
					.then(Commands.literal("enable")
						//? if >= 1.21.11 {
						.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
						//?} else {
						/*.requires(source -> source.hasPermission(2))
						 *///?}
						.executes(ctx -> {
							Structurify.getConfig().getDebugData().setEnabled(true);
							Structurify.getConfig().getDebugData().setDebugMode(DebugData.DebugMode.FLATNESS);
							Structurify.getConfig().getDebugData().setSamplingMode(DebugData.SamplingMode.FINAL);
							reloadStructureChecks(ctx);

							ctx.getSource().sendSuccess(
								() -> Component.literal("Structurify debug enabled."),
								!ctx.getSource().isPlayer()
							);

							return 1;
						})
					)
					.then(Commands.literal("disable")
						//? if >= 1.21.11 {
						.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
						//?} else {
						/*.requires(source -> source.hasPermission(2))
						 *///?}
						.executes(ctx -> {
							Structurify.getConfig().getDebugData().setEnabled(false);
							Structurify.getConfig().getDebugData().setDebugMode(DebugData.DebugMode.NONE);
							reloadStructureChecks(ctx);

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
							//? if >= 1.21.11 {
							.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
							//?} else {
							/*.requires(source -> source.hasPermission(2))
							 *///?}
							.executes(ctx -> {
								var raw = StringArgumentType.getString(ctx, "debugMode");

								DebugData.DebugMode debugMode;
								try {
									debugMode = DebugData.DebugMode.valueOf(raw.toUpperCase(java.util.Locale.ROOT));
								} catch (IllegalArgumentException ex) {
									ctx.getSource().sendFailure(Component.literal("Unknown debug mode: " + raw));
									return 0;
								}

								Structurify.getConfig().getDebugData().setEnabled(true);
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
							//? if >= 1.21.11 {
							.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
							//?} else {
							/*.requires(source -> source.hasPermission(2))
							 *///?}
							.executes(ctx -> {
								var raw = StringArgumentType.getString(ctx, "samplingMode");

								DebugData.SamplingMode samplingMode;

								try {
									samplingMode = DebugData.SamplingMode.valueOf(raw.toUpperCase(java.util.Locale.ROOT));
								} catch (IllegalArgumentException ex) {
									ctx.getSource().sendFailure(Component.literal("Unknown sampling mode: " + raw));
									return 0;
								}

								Structurify.getConfig().getDebugData().setEnabled(true);
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
				)
		);
	}

	private static void reloadStructureChecks(CommandContext<CommandSourceStack> ctx) {
		var serverLevel = ctx.getSource().getLevel();
		var blockPos = BlockPos.containing(ctx.getSource().getPosition());
		var chunkSource = serverLevel.getChunkSource();
		var chunkGenerator = chunkSource.getGenerator();
		var biomeSource = chunkGenerator.getBiomeSource();
		var randomState = chunkSource.randomState();

		int viewDistance = ctx.getSource().getServer().getPlayerList().getViewDistance();
		int chunkRadius = Math.max(1, (int) (viewDistance * 1.33));

		int baseChunkX = SectionPos.blockToSectionCoord(blockPos.getX());
		int baseChunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());

		for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
			for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
				int chunkX = baseChunkX + dx;
				int chunkZ = baseChunkZ + dz;

				var chunk = chunkSource.getChunkNow(chunkX, chunkZ);
				if (chunk == null) {
					continue;
				}

				for (var structureStartEntry : chunk.getAllStarts().entrySet()) {
					StructureChecker.debugCheckStructure(
						structureStartEntry.getValue(),
						(StructurifyStructure) structureStartEntry.getKey(),
						chunkGenerator,
						serverLevel,
						randomState,
						biomeSource
					);
				}
			}
		}
	}

	private static int getStructureList(CommandSourceStack source) {
		ServerLevel level = source.getLevel();
		BlockPos commandPos = BlockPos.containing(source.getPosition());
		//? if > 1.21.1 {
		var structureRegistry = source.getLevel().registryAccess().lookupOrThrow(Registries.STRUCTURE);
		//?} else {
		/*var structureRegistry = source.getLevel().registryAccess().registryOrThrow(Registries.STRUCTURE);
		 *///?}

		var structureStarts = level.structureManager().startsForStructure(ChunkPosUtil.createChunkPos(commandPos), structure -> true).stream().filter(structureStart -> structureStart.getBoundingBox().inflatedBy(16).isInside(commandPos)).toList();

		if (structureStarts.isEmpty()) {
			source.sendSuccess(() ->  Component.literal("There is no structures at ").append(getClickablePos(commandPos)).append(Component.literal(".")), !source.isPlayer());
			return 1;
		}

		var foundStructures = Component.literal("List of structures at ").append(getClickablePos(commandPos)).append(Component.literal(":"));

		for (var structureStart : structureStarts) {
			var structure = structureStart.getStructure();
			var structureId = structureRegistry.getKey(structure);

			if(structureId == null) {
				continue;
			}

			foundStructures.append(Component.literal("\n - ").withStyle(ChatFormatting.RESET))
				.append(getClickableStructure(structureId))
				.append(Component.literal(" ").append(getClickablePos(structureStart.getChunkPos().getMiddleBlockPosition(commandPos.getY()))));
		}

		source.sendSuccess(() -> foundStructures, !source.isPlayer());
		return 1;
	}

	private static int changeStructure(
		CommandSourceStack source,
		ResourceKey<Structure> structure,
		boolean isDisabled
	) {
		var structureId = structure/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();
		var config = Structurify.getConfig();

		if(!config.getStructureData().containsKey(structureId)) {
			return 1;
		}

		StructureData structureData = config.getStructureData().get(structureId);

		if(structureData.isDisabled() == isDisabled) {
			source.sendSuccess(() -> Component.literal("Structure " + structureId + " is already " + (isDisabled ? "disabled" : "enabled") +"."), !source.isPlayer());
			return 1;
		}

		structureData.setDisabled(isDisabled);
		config.save();

		source.sendSuccess(() -> Component.literal("Structure " + structureId + " " + (isDisabled ? "disabled" : "enabled") +"."), !source.isPlayer());
		return 1;
	}

	private static Component getClickableStructure(Identifier structureId) {
		if(Structurify.getConfig().getStructureData().containsKey(structureId.toString())) {
			if(Structurify.getConfig().getStructureData().get(structureId.toString()).isDisabled()) {
				return Component.literal(structureId.toString()).withStyle((style) -> style.withColor(ChatFormatting.RED).withClickEvent(ClickEventFactory.createRunCommand("/structurify structure enable " + structureId)).withHoverEvent(HoverEventFactory.createShowText(Component.literal("Click to enable \"" + structureId + "\" structure generation"))));
			}

			return Component.literal(structureId.toString()).withStyle((style) -> style.withColor(ChatFormatting.GREEN).withClickEvent(ClickEventFactory.createRunCommand("/structurify structure disable " + structureId)).withHoverEvent(HoverEventFactory.createShowText(Component.literal("Click to disable \"" + structureId + "\" structure generation"))));
		}

		return Component.literal(structureId.toString()).withStyle((style) -> style.withColor(ChatFormatting.GOLD));
	}

	private static Component getClickablePos(BlockPos blockPos) {
		return ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", blockPos.getX(), blockPos.getY(), blockPos.getZ())).withStyle((style) -> style.withColor(ChatFormatting.GREEN).withClickEvent(ClickEventFactory.createSuggestCommand("/tp @s " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ())).withHoverEvent(HoverEventFactory.createShowText(Component.translatable("chat.coordinates.tooltip"))));
	}

	private static int locateStructure(
		CommandSourceStack source,
		Result<Structure> structure
	) throws CommandSyntaxException {
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