package com.faboslav.structurify.common.config.data.structure;

import com.faboslav.structurify.common.config.data.structure.jigsaw.HeightProviderData;
import com.faboslav.structurify.common.config.data.structure.jigsaw.ProjectStartToHeightmap;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class JigsawData
{
	public static final int MIN_SIZE = 0;
	public static final int MAX_SIZE = 20;
	public static final int MIN_HORIZONTAL_MAX_DISTANCE_FROM_CENTER = 1;
	public static final int MAX_HORIZONTAL_MAX_DISTANCE_FROM_CENTER = 128;
	public static final int MIN_VERTICAL_MAX_DISTANCE_FROM_CENTER = 1;
	public static final int MAX_VERTICAL_MAX_DISTANCE_FROM_CENTER = 4096;

	private final @Nullable Integer defaultSize;
	private final @Nullable Integer defaultHorizontalMaxDistanceFromCenter;
	private final @Nullable Integer defaultVerticalMaxDistanceFromCenter;
	private final @Nullable HeightProviderData defaultHeightProviderData;
	private final @Nullable ProjectStartToHeightmap defaultProjectStartToHeightmap;

	private @Nullable Integer size;
	private @Nullable Integer horizontalMaxDistanceFromCenter;
	private @Nullable Integer verticalMaxDistanceFromCenter;
	private @Nullable HeightProviderData heightProviderData;
	private @Nullable ProjectStartToHeightmap projectStartToHeightmap;

	public JigsawData() {
		defaultSize = null;
		defaultHorizontalMaxDistanceFromCenter = null;
		defaultVerticalMaxDistanceFromCenter = null;
		defaultHeightProviderData = null;
		defaultProjectStartToHeightmap = null;
	}

	public JigsawData(@Nullable Integer size, @Nullable Integer horizontalMaxDistanceFromCenter, @Nullable Integer verticalMaxDistanceFromCenter, @Nullable HeightProviderData heightProviderData, @Nullable ProjectStartToHeightmap projectStartToHeightmap) {
		this.defaultSize = size;
		this.size = size;
		this.defaultHorizontalMaxDistanceFromCenter = horizontalMaxDistanceFromCenter;
		this.horizontalMaxDistanceFromCenter = horizontalMaxDistanceFromCenter;
		this.defaultVerticalMaxDistanceFromCenter = verticalMaxDistanceFromCenter;
		this.verticalMaxDistanceFromCenter = verticalMaxDistanceFromCenter;
		if(heightProviderData != null) {
			this.defaultHeightProviderData = heightProviderData.clone();
			this.heightProviderData = heightProviderData.clone();
		} else {
			this.defaultHeightProviderData = null;
			this.heightProviderData = null;
		}
		this.defaultProjectStartToHeightmap = projectStartToHeightmap;
		this.projectStartToHeightmap = projectStartToHeightmap;
	}

	public boolean isUsingDefaultValues() {
		return this.isUsingDefaultSize()
			   && this.isUsingDefaultMaxDistanceFromCenter()
			   && this.isUsingDefaultHeightProvider()
			   && this.isUsingDefaultProjectStartToHeightmap();
	}

	public boolean isUsingDefaultSize() {
		return Objects.equals(this.size, this.defaultSize);
	}

	public boolean isUsingDefaultMaxDistanceFromCenter() {
		return Objects.equals(this.horizontalMaxDistanceFromCenter, this.defaultHorizontalMaxDistanceFromCenter) && Objects.equals(this.verticalMaxDistanceFromCenter, this.defaultVerticalMaxDistanceFromCenter);
	}

	public boolean isUsingDefaultHeightProvider() {
		return Objects.equals(this.heightProviderData, this.defaultHeightProviderData);
	}

	public boolean isUsingDefaultProjectStartToHeightmap() {
		return Objects.equals(this.projectStartToHeightmap, this.defaultProjectStartToHeightmap);
	}

	public boolean isUsingSize() {
		return this.defaultSize != null && this.size != null;
	}

	public boolean isUsingMaxDistanceFromCenter() {
		return this.defaultHorizontalMaxDistanceFromCenter != null && this.horizontalMaxDistanceFromCenter != null && this.defaultVerticalMaxDistanceFromCenter != null && this.verticalMaxDistanceFromCenter != null;
	}

	public boolean isUsingHeightProvider() {
		return this.heightProviderData != null;
	}

	public boolean isUsingProjectStartToHeightmap() {
		return this.projectStartToHeightmap != null;
	}

	@Nullable
	public Integer getDefaultSize() {
		return this.defaultSize;
	}

	@Nullable
	public Integer getSize() {
		return size;
	}

	public void setSize(@Nullable Integer size) {
		this.size = size;
	}

	public @Nullable Integer getDefaultHorizontalMaxDistanceFromCenter() {
		return this.defaultHorizontalMaxDistanceFromCenter;
	}

	public @Nullable Integer getHorizontalMaxDistanceFromCenter() {
		return this.horizontalMaxDistanceFromCenter;
	}

	public void setHorizontalMaxDistanceFromCenter(int horizontalMaxDistanceFromCenter) {
		this.horizontalMaxDistanceFromCenter = horizontalMaxDistanceFromCenter;
	}

	public @Nullable Integer getDefaultVerticalMaxDistanceFromCenter() {
		return this.defaultVerticalMaxDistanceFromCenter;
	}

	public @Nullable Integer getVerticalMaxDistanceFromCenter() {
		return this.verticalMaxDistanceFromCenter;
	}

	public void setVerticalMaxDistanceFromCenter(int verticalMaxDistanceFromCenter) {
		this.verticalMaxDistanceFromCenter = verticalMaxDistanceFromCenter;
	}

	public @Nullable HeightProviderData getDefaultHeightProviderData() {
		return this.defaultHeightProviderData;
	}

	public @Nullable HeightProviderData getHeightProviderData() {
		return this.heightProviderData;
	}

	public void setHeightProviderData(@Nullable HeightProviderData heightProviderData) {
		this.heightProviderData = heightProviderData;
	}

	public @Nullable ProjectStartToHeightmap getDefaultProjectStartToHeightmap() {
		return this.defaultProjectStartToHeightmap;
	}

	public @Nullable ProjectStartToHeightmap getProjectStartToHeightmap() {
		return this.projectStartToHeightmap;
	}

	public void setProjectStartToHeightmap(@Nullable ProjectStartToHeightmap projectStartToHeightmap) {
		this.projectStartToHeightmap = projectStartToHeightmap;
	}
}
