package com.faboslav.structurify.fabric.tests;

import com.faboslav.structurify.common.Structurify;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.PermissionSet;

import java.util.ArrayList;
import java.util.List;

public class StructureLocateTest
{
	@GameTest
	public void structureLocateTest(GameTestHelper context) {

		var level = context.getLevel();
		var server = level.getServer();
		List<Component> output = new ArrayList<>();
		final boolean[] success = {false};
		final int[] result = {0};
		CommandSource recordingSource = new CommandSource() {
			@Override
			public void sendSystemMessage(Component message) {
				output.add(message);
			}
			@Override
			public boolean acceptsSuccess() {
				return true;
			}
			@Override
			public boolean acceptsFailure() {
				return true;
			}
			@Override
			public boolean shouldInformAdmins() {
				return false;
			}
		};
		CommandSourceStack source = server.createCommandSourceStack()
			.withSource(recordingSource)
			.withLevel(level)
			.withPosition(context.absolutePos(BlockPos.ZERO).getCenter())
			.withPermission(PermissionSet.ALL_PERMISSIONS)
			.withCallback((wasSuccessful, returnValue) -> {
				success[0] = wasSuccessful;
				result[0] = returnValue;
			});
		server.getCommands().performPrefixedCommand(
			source,
			"locate structure structurify:your_structure"
		);
		for (Component message : output) {
			context.assertTrue(message.getString().contains("There is no structure with type"), "fail");
		}
		context.succeed();

	}
}
