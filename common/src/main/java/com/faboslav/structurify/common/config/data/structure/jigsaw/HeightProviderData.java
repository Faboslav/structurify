package com.faboslav.structurify.common.config.data.structure.jigsaw;

import com.faboslav.structurify.common.mixin.level.BiasedToBottomHeightAccessor;
import com.faboslav.structurify.common.mixin.level.TrapezoidHeightAccessor;
import com.faboslav.structurify.common.mixin.level.UniformHeightAccessor;
import com.faboslav.structurify.common.mixin.level.VeryBiasedToBottomHeightAccessor;
import net.minecraft.world.level.levelgen.heightproviders.*;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
		this.minInclusive = minInclusive == null ? MIN_INCLUSIVE_DEFAULT_VALUE.clone() : minInclusive;
		this.maxInclusive = maxInclusive == null ? MAX_INCLUSIVE_DEFAULT_VALUE.clone() : maxInclusive;
		this.value = value == null ? VALUE_DEFAULT_VALUE.clone() : value;
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
		if (provider == null) {
			return null;
		}

		var providerClass = provider.getClass();

		if (providerClass == ConstantHeight.class) {
			var value = VerticalAnchorData.fromAnchor(((ConstantHeight) provider).getValue());

			if (value == null) {
				return null;
			}

			return new HeightProviderData(
				Type.CONSTANT,
				null,
				null,
				value,
				null,
				null
			);
		} else if (providerClass == UniformHeight.class) {
			var accessor = (UniformHeightAccessor) provider;
			var minInclusive = VerticalAnchorData.fromAnchor(accessor.getMinInclusive());
			var maxInclusive = VerticalAnchorData.fromAnchor(accessor.getMaxInclusive());

			if (minInclusive == null || maxInclusive == null) {
				return null;
			}

			return new HeightProviderData(
				Type.UNIFORM,
				minInclusive,
				maxInclusive,
				null,
				null,
				null
			);
		} else if (providerClass == TrapezoidHeight.class) {
			var accessor = (TrapezoidHeightAccessor) provider;
			var minInclusive = VerticalAnchorData.fromAnchor(accessor.getMinInclusive());
			var maxInclusive = VerticalAnchorData.fromAnchor(accessor.getMaxInclusive());

			if (minInclusive == null || maxInclusive == null) {
				return null;
			}

			return new HeightProviderData(
				Type.TRAPEZOID,
				minInclusive,
				maxInclusive,
				null,
				accessor.getPlateau(),
				null
			);
		} else if (providerClass == BiasedToBottomHeight.class) {
			var accessor = (BiasedToBottomHeightAccessor) provider;
			var minInclusive = VerticalAnchorData.fromAnchor(accessor.getMinInclusive());
			var maxInclusive = VerticalAnchorData.fromAnchor(accessor.getMaxInclusive());

			if (minInclusive == null || maxInclusive == null) {
				return null;
			}

			return new HeightProviderData(
				Type.BIASED_TO_BOTTOM,
				minInclusive,
				maxInclusive,
				null,
				null,
				accessor.getInner()
			);
		} else if (providerClass == VeryBiasedToBottomHeight.class) {
			var accessor = (VeryBiasedToBottomHeightAccessor) provider;
			var minInclusive = VerticalAnchorData.fromAnchor(accessor.getMinInclusive());
			var maxInclusive = VerticalAnchorData.fromAnchor(accessor.getMaxInclusive());

			if (minInclusive == null || maxInclusive == null) {
				return null;
			}

			return new HeightProviderData(
				Type.VERY_BIASED_TO_BOTTOM,
				minInclusive,
				maxInclusive,
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
		this.minInclusive = minInclusive == null ? MIN_INCLUSIVE_DEFAULT_VALUE.clone() : minInclusive;
	}

	public VerticalAnchorData getMaxInclusive() {
		return this.maxInclusive;
	}

	public void setMaxInclusive(VerticalAnchorData maxInclusive) {
		this.maxInclusive = maxInclusive == null ? MAX_INCLUSIVE_DEFAULT_VALUE.clone() : maxInclusive;
	}

	public VerticalAnchorData getValue() {
		return this.value;
	}

	public void setValue(VerticalAnchorData value) {
		this.value = value == null ? VALUE_DEFAULT_VALUE.clone() : value;
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
			   && Objects.equals(this.minInclusive, heightProviderData.minInclusive)
			   && Objects.equals(this.maxInclusive, heightProviderData.maxInclusive)
			   && Objects.equals(this.value, heightProviderData.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			this.type,
			this.minInclusive,
			this.maxInclusive,
			this.value,
			this.plateau,
			this.inner
		);
	}
}