package com.faboslav.structurify.common.config.client.api.option;


import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@SuppressWarnings({"all", "unchecked"})
public final class InvisibleOptionGroup implements OptionGroup {
	private final @NotNull Component name;
	private final @NotNull OptionDescription description;
	private final ImmutableList<? extends Option<?>> options;
	private final boolean collapsed;
	private final boolean isRoot;

	public InvisibleOptionGroup(@NotNull Component name, @NotNull OptionDescription description, ImmutableList<? extends Option<?>> options, boolean collapsed, boolean isRoot) {
		this.name = name;
		this.description = description;
		this.options = options;
		this.collapsed = collapsed;
		this.isRoot = isRoot;
	}

	@Override
	public Component name() {
		return this.name;
	}

	@Override
	public OptionDescription description() {
		return this.description;
	}

	public @NotNull Component tooltip() {
		return this.description.text();
	}

	public @NotNull ImmutableList<? extends Option<?>> options() {
		return this.options;
	}

	public boolean collapsed() {
		return this.collapsed;
	}

	public boolean isRoot() {
		return this.isRoot;
	}

	@Internal
	public static final class Builder implements OptionGroup.Builder {
		private Component name = Component.empty();
		private OptionDescription description;
		private final List<Option<?>> options;
		private boolean collapsed;

		public Builder() {
			this.description = OptionDescription.EMPTY;
			this.options = new ArrayList();
			this.collapsed = false;
		}


		@Override
		public OptionGroup.Builder name(@NotNull Component component) {
			return this;
		}

		@Override
		public OptionGroup.Builder description(@NotNull OptionDescription optionDescription) {
			return this;
		}

		public InvisibleOptionGroup.Builder option(@NotNull Option<?> option) {
			Validate.notNull(option, "`option` must not be null", new Object[0]);
			this.options.add(option);
			return this;
		}

		public InvisibleOptionGroup.Builder options(@NotNull Collection<? extends Option<?>> options) {
			Validate.notEmpty(options, "`options` must not be empty", new Object[0]);
			Stream var10000 = options.stream();
			Objects.requireNonNull(ListOption.class);
				this.options.addAll(options);
				return this;
		}

		public InvisibleOptionGroup.Builder collapsed(boolean collapsible) {
			this.collapsed = collapsible;
			return this;
		}

		public InvisibleOptionGroup build() {
			return new InvisibleOptionGroup(this.name, this.description, ImmutableList.copyOf(this.options), this.collapsed, true);
		}
	}
}
