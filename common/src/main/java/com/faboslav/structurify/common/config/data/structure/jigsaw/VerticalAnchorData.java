package com.faboslav.structurify.common.config.data.structure.jigsaw;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class VerticalAnchorData
{
	private Type type;
	private @Nullable Integer value;

	public VerticalAnchorData(Type type, @Nullable Integer value) {
		this.type = type;
		this.value = value;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@Nullable
	public Integer getValue() {
		return this.value;
	}

	public void setValue(@Nullable Integer value) {
		this.value = value;
	}

	public VerticalAnchor toVerticalAnchor() {
		return switch (this.type) {
			case ABSOLUTE -> VerticalAnchor.absolute(this.value == null ? 0 : this.value);
			case ABOVE_BOTTOM -> VerticalAnchor.aboveBottom(this.value == null ? 0 : this.value);
			case BELOW_TOP -> VerticalAnchor.belowTop(this.value == null ? 0 : this.value);
			case TOP -> VerticalAnchor.top();
			case BOTTOM -> VerticalAnchor.bottom();
		};
	}

	@Nullable
	public static VerticalAnchorData fromAnchor(VerticalAnchor anchor) {
		if (anchor == VerticalAnchor.top()) {
			return new VerticalAnchorData(Type.TOP, null);
		} else if (anchor == VerticalAnchor.bottom()) {
			return new VerticalAnchorData(Type.BOTTOM, null);
		} else if (anchor instanceof VerticalAnchor.Absolute absolute) {
			return new VerticalAnchorData(Type.ABSOLUTE, absolute.y());
		} else if (anchor instanceof VerticalAnchor.AboveBottom aboveBottom) {
			return new VerticalAnchorData(Type.ABOVE_BOTTOM, aboveBottom.offset());
		} else if (anchor instanceof VerticalAnchor.BelowTop belowTop) {
			return new VerticalAnchorData(Type.BELOW_TOP, belowTop.offset());
		}

		return null;
	}

	public enum Type
	{
		ABSOLUTE,
		ABOVE_BOTTOM,
		BELOW_TOP,
		TOP,
		BOTTOM;

		public VerticalAnchorData.Type toDataType() {
			return switch (this) {
				case ABSOLUTE -> VerticalAnchorData.Type.ABSOLUTE;
				case ABOVE_BOTTOM -> VerticalAnchorData.Type.ABOVE_BOTTOM;
				case BELOW_TOP -> VerticalAnchorData.Type.BELOW_TOP;
				case TOP -> VerticalAnchorData.Type.TOP;
				case BOTTOM -> VerticalAnchorData.Type.BOTTOM;
			};
		}

		public static Type fromDataType(VerticalAnchorData.Type type) {
			return switch (type) {
				case ABSOLUTE -> ABSOLUTE;
				case ABOVE_BOTTOM -> ABOVE_BOTTOM;
				case BELOW_TOP -> BELOW_TOP;
				case TOP -> TOP;
				case BOTTOM -> BOTTOM;
			};
		}
	}

	public enum ConstantVerticalAnchorType
	{
		ABSOLUTE,
		ABOVE_BOTTOM,
		BELOW_TOP;

		public VerticalAnchorData.Type toDataType() {
			return switch (this) {
				case ABSOLUTE -> VerticalAnchorData.Type.ABSOLUTE;
				case ABOVE_BOTTOM -> VerticalAnchorData.Type.ABOVE_BOTTOM;
				case BELOW_TOP -> VerticalAnchorData.Type.BELOW_TOP;
			};
		}

		public static ConstantVerticalAnchorType fromDataType(VerticalAnchorData.Type type) {
			return switch (type) {
				case ABSOLUTE -> ABSOLUTE;
				case ABOVE_BOTTOM -> ABOVE_BOTTOM;
				case BELOW_TOP -> BELOW_TOP;
				default -> ABSOLUTE;
			};
		}
	}

	@Override
	public VerticalAnchorData clone() {
		return new VerticalAnchorData(this.type, this.value);
	}

	@Override
	public boolean equals(Object possibleVerticalAnchorData) {
		if (this == possibleVerticalAnchorData) {
			return true;
		}

		if (!(possibleVerticalAnchorData instanceof VerticalAnchorData verticalAnchorData)) {
			return false;
		}

		return this.type == verticalAnchorData.type && Objects.equals(this.value, verticalAnchorData.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.type, this.value);
	}
}