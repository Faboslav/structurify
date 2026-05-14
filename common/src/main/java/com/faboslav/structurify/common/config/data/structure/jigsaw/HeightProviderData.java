package com.faboslav.structurify.common.config.data.structure.jigsaw;

import com.faboslav.structurify.common.mixin.level.BiasedToBottomHeightAccessor;
import com.faboslav.structurify.common.mixin.level.TrapezoidHeightAccessor;
import com.faboslav.structurify.common.mixin.level.UniformHeightAccessor;
import com.faboslav.structurify.common.mixin.level.VeryBiasedToBottomHeightAccessor;
import net.minecraft.world.level.levelgen.heightproviders.*;
import org.jetbrains.annotations.Nullable;

public final class HeightProviderData
{
	private static final VerticalAnchorData VALUE_DEFAULT_VALUE = new VerticalAnchorData(VerticalAnchorData.Type.ABSOLUTE, 0);
	private static final VerticalAnchorData MIN_INCLUSIVE_DEFAULT_VALUE = new VerticalAnchorData(VerticalAnchorData.Type.ABSOLUTE, 0);
	private static final VerticalAnchorData MAX_INCLUSIVE_DEFAULT_VALUE = new VerticalAnchorData(VerticalAnchorData.Type.ABSOLUTE, 80);
	private static final int PLATEAU_DEFAULT_VALUE = 0;
	private static final int INNER_DEFAULT_VALUE = 1;

	private Type type;
	private VerticalAnchorData minInclusive;
	private VerticalAnchorData maxInclusive;
	private VerticalAnchorData value;
	private int plateau;
	private int inner;

	public HeightProviderData(
		Type type,
		VerticalAnchorData minInclusive,
		VerticalAnchorData maxInclusive,
		VerticalAnchorData value,
		Integer plateau,
		Integer inner
	) {
		this.type = type;
		this.minInclusive = minInclusive == null ? MIN_INCLUSIVE_DEFAULT_VALUE : minInclusive;
		this.maxInclusive = maxInclusive == null ? MAX_INCLUSIVE_DEFAULT_VALUE : maxInclusive;
		this.value = value == null ? VALUE_DEFAULT_VALUE : value;
		this.plateau = plateau == null ? PLATEAU_DEFAULT_VALUE : plateau;
		this.inner = inner == null ? INNER_DEFAULT_VALUE : inner;
	}

	public HeightProvider toHeightProvider() {
		return switch (this.type) {
			case CONSTANT -> ConstantHeight.of(this.value.toVerticalAnchor());
			case UNIFORM -> UniformHeight.of(
				this.minInclusive.toVerticalAnchor(),
				this.maxInclusive.toVerticalAnchor()
			);
			case TRAPEZOID -> TrapezoidHeight.of(
				this.minInclusive.toVerticalAnchor(),
				this.maxInclusive.toVerticalAnchor(),
				this.plateau
			);
			case BIASED_TO_BOTTOM -> BiasedToBottomHeight.of(
				this.minInclusive.toVerticalAnchor(),
				this.maxInclusive.toVerticalAnchor(),
				this.inner
			);
			case VERY_BIASED_TO_BOTTOM -> VeryBiasedToBottomHeight.of(
				this.minInclusive.toVerticalAnchor(),
				this.maxInclusive.toVerticalAnchor(),
				this.inner
			);
		};
	}

	@Nullable
	public static HeightProviderData fromHeightProvider(@Nullable HeightProvider provider) {
		if (provider instanceof ConstantHeight constantHeight) {
			return new HeightProviderData(
				Type.CONSTANT,
				null,
				null,
				VerticalAnchorData.fromAnchor(constantHeight.getValue()),
				null,
				null
			);
		} else if (provider instanceof UniformHeight uniformHeight) {
			var accessor = (UniformHeightAccessor) uniformHeight;

			return new HeightProviderData(
				Type.UNIFORM,
				VerticalAnchorData.fromAnchor(accessor.getMinInclusive()),
				VerticalAnchorData.fromAnchor(accessor.getMaxInclusive()),
				null,
				null,
				null
			);
		} else if (provider instanceof TrapezoidHeight trapezoidHeight) {
			var accessor = (TrapezoidHeightAccessor) trapezoidHeight;

			return new HeightProviderData(
				Type.TRAPEZOID,
				VerticalAnchorData.fromAnchor(accessor.getMinInclusive()),
				VerticalAnchorData.fromAnchor(accessor.getMaxInclusive()),
				null,
				accessor.getPlateau(),
				null
			);
		} else if (provider instanceof BiasedToBottomHeight biasedToBottomHeight) {
			var accessor = (BiasedToBottomHeightAccessor) biasedToBottomHeight;

			return new HeightProviderData(
				Type.BIASED_TO_BOTTOM,
				VerticalAnchorData.fromAnchor(accessor.getMinInclusive()),
				VerticalAnchorData.fromAnchor(accessor.getMaxInclusive()),
				null,
				null,
				accessor.getInner()
			);
		} else if (provider instanceof VeryBiasedToBottomHeight veryBiasedToBottomHeight) {
			var accessor = (VeryBiasedToBottomHeightAccessor) veryBiasedToBottomHeight;

			return new HeightProviderData(
				Type.VERY_BIASED_TO_BOTTOM,
				VerticalAnchorData.fromAnchor(accessor.getMinInclusive()),
				VerticalAnchorData.fromAnchor(accessor.getMaxInclusive()),
				null,
				null,
				accessor.getInner()
			);
		}

		return null;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public VerticalAnchorData getMinInclusive() {
		return this.minInclusive;
	}

	public void setMinInclusive(VerticalAnchorData minInclusive) {
		this.minInclusive = minInclusive == null ? MIN_INCLUSIVE_DEFAULT_VALUE : minInclusive;
	}

	public VerticalAnchorData getMaxInclusive() {
		return this.maxInclusive;
	}

	public void setMaxInclusive(VerticalAnchorData maxInclusive) {
		this.maxInclusive = maxInclusive == null ? MAX_INCLUSIVE_DEFAULT_VALUE : maxInclusive;
	}

	public VerticalAnchorData getValue() {
		return this.value;
	}

	public void setValue(VerticalAnchorData value) {
		this.value = value == null ? VALUE_DEFAULT_VALUE : value;
	}

	public Integer getPlateau() {
		return this.plateau;
	}

	public void setPlateau(Integer plateau) {
		this.plateau = plateau == null ? PLATEAU_DEFAULT_VALUE : plateau;
	}

	public Integer getInner() {
		return this.inner;
	}

	public void setInner(Integer inner) {
		this.inner = inner == null ? INNER_DEFAULT_VALUE : inner;
	}

	public enum Type
	{
		CONSTANT,
		UNIFORM,
		TRAPEZOID,
		BIASED_TO_BOTTOM,
		VERY_BIASED_TO_BOTTOM
	}

	@Override
	public HeightProviderData clone() {
		return new HeightProviderData(
			this.type,
			this.minInclusive == null ? null : this.minInclusive.clone(),
			this.maxInclusive == null ? null : this.maxInclusive.clone(),
			this.value == null ? null : this.value.clone(),
			this.plateau,
			this.inner
		);
	}

	@Override
	public boolean equals(Object possibleHeightProviderData) {
		if (this == possibleHeightProviderData) {
			return true;
		}

		if (!(possibleHeightProviderData instanceof HeightProviderData heightProviderData)) {
			return false;
		}

		return this.plateau == heightProviderData.plateau
			   && this.inner == heightProviderData.inner
			   && this.type == heightProviderData.type
			   && java.util.Objects.equals(this.minInclusive, heightProviderData.minInclusive)
			   && java.util.Objects.equals(this.maxInclusive, heightProviderData.maxInclusive)
			   && java.util.Objects.equals(this.value, heightProviderData.value);
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(
			this.type,
			this.minInclusive,
			this.maxInclusive,
			this.value,
			this.plateau,
			this.inner
		);
	}
}