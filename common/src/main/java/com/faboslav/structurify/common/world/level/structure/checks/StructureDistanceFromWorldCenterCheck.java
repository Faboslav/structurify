package com.faboslav.structurify.common.world.level.structure.checks;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.config.data.StructureData;
import com.faboslav.structurify.common.config.data.StructureNamespaceData;
import com.faboslav.structurify.common.config.data.structure.DistanceFromWorldCenterCheckData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class StructureDistanceFromWorldCenterCheck
{
	@Nullable
	public static DistanceFromWorldCenterCheckData getDistanceFromWorldCenterData(
		Identifier structureId,
		StructureData structureData
	) {
		var globalStructureData = Structurify.getConfig().getStructureNamespaceData().get(StructureNamespaceData.GLOBAL_NAMESPACE_IDENTIFIER);
		var namespacedStructureData = Structurify.getConfig().getStructureNamespaceData().get(structureId.getNamespace());

		var globalDistanceFromWorldCenterData = globalStructureData.getDistanceFromWorldCenterCheckData();
		var namespaceDistanceFromWorldCenterData = namespacedStructureData.getDistanceFromWorldCenterCheckData();
		var structureDistanceFromWorldCenterData = structureData.getDistanceFromWorldCenterCheckData();

		DistanceFromWorldCenterCheckData distanceFromWorldCenterCheckDataToCheck = globalDistanceFromWorldCenterData;

		if (namespaceDistanceFromWorldCenterData.isOverridingGlobalDistanceFromWorldCenter()) {
			distanceFromWorldCenterCheckDataToCheck = namespaceDistanceFromWorldCenterData;
		}

		if (structureDistanceFromWorldCenterData.isOverridingGlobalDistanceFromWorldCenter()) {
			distanceFromWorldCenterCheckDataToCheck = structureDistanceFromWorldCenterData;
		}

		return distanceFromWorldCenterCheckDataToCheck;
	}

	public static boolean checkDistanceFromWorldCenter(
		DistanceFromWorldCenterCheckData distanceFromWorldCenterCheckData,
		ChunkPos chunkPos
	) {
		if (!distanceFromWorldCenterCheckData.isEnabled()) {
			return true;
		}

		boolean enableMinDistanceFromWorldCenter = distanceFromWorldCenterCheckData.isMinDistanceFromWorldCenterEnabled();
		boolean enableMaxDistanceFromWorldCenter = distanceFromWorldCenterCheckData.isMaxDistanceFromWorldCenterEnabled();
		double minDistanceFromWorldCenter = distanceFromWorldCenterCheckData.getMinDistanceFromWorldCenter();
		double maxDistanceFromWorldCenter = distanceFromWorldCenterCheckData.getMaxDistanceFromWorldCenter();

		var worldPosition = chunkPos.getWorldPosition();

		if (distanceFromWorldCenterCheckData.getMode() == DistanceFromWorldCenterCheckData.DistanceFromWorldCenterCheckMode.SQUARE) {
			var distanceFromWorldCenter = Math.max(Math.abs(worldPosition.getX()), Math.abs(worldPosition.getZ()));
			var isFarEnoughFromWorldCenter = !enableMinDistanceFromWorldCenter || (distanceFromWorldCenter >= minDistanceFromWorldCenter);
			var isCloseEnoughToWorldCenter = !enableMaxDistanceFromWorldCenter || (distanceFromWorldCenter <= maxDistanceFromWorldCenter);

			return isFarEnoughFromWorldCenter && isCloseEnoughToWorldCenter;
		}

		var distanceFromWorldCenter = worldPosition.distSqr(new BlockPos(0, worldPosition.getY(), 0));
		var isFarEnoughFromWorldCenter = !enableMinDistanceFromWorldCenter || (distanceFromWorldCenter >= minDistanceFromWorldCenter * minDistanceFromWorldCenter);
		var isCloseEnoughToWorldCenter = !enableMaxDistanceFromWorldCenter || (distanceFromWorldCenter <= maxDistanceFromWorldCenter * maxDistanceFromWorldCenter);

		return isFarEnoughFromWorldCenter && isCloseEnoughToWorldCenter;
	}
}
