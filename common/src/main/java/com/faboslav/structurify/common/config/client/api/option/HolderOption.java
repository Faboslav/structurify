package com.faboslav.structurify.common.config.client.api.option;

import com.faboslav.structurify.common.config.client.api.controller.DualController;
import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class HolderOption<K extends Option<?>, V extends Option<?>> implements Option<OptionPair<K, V>>
{
	private final Component name;
	private OptionDescription description;
	private final DualController<K, V> controller;
	private final OptionPair<K, V> optionPair;
	private final K firstOption;
	private final V secondOption;
	private boolean available;

	private final ImmutableSet<OptionFlag> flags;

	private final List<BiConsumer<Option<OptionPair<K, V>>, OptionPair<K, V>>> listeners;
	private int listenerTriggerDepth = 0;

	public HolderOption(
		@NotNull Component name,
		@NotNull Function<OptionPair<K, V>, OptionDescription> descriptionFunction,
		@NotNull Function<Option<OptionPair<K, V>>, DualController<K, V>> controlGetter,
		boolean available,
		ImmutableSet<OptionFlag> flags,
		@NotNull Collection<BiConsumer<Option<OptionPair<K, V>>, OptionPair<K, V>>> listeners
	) {
		this.name = name;
		this.available = available;
		this.flags = flags;
		this.listeners = new ArrayList<>(listeners);

		this.controller = controlGetter.apply(this);
		this.optionPair = this.controller.optionPair;
		this.firstOption = this.controller.optionPair.getFirstOption();
		this.secondOption = this.controller.optionPair.getSecondOption();

		optionPair.getFirstOption().addListener((opt, pending) -> triggerListeners(false));
		optionPair.getSecondOption().addListener((opt, pending) -> triggerListeners(false));

		addListener((opt, pending) -> description = descriptionFunction.apply(pending));
		triggerListeners(true);
	}

	@Override
	public @NotNull Component name() {
		return optionPair.getFirstOption().name().copy().append(" & ").append(optionPair.getSecondOption().name().copy());
	}

	@Override
	public @NotNull OptionDescription description() {
		return OptionDescription.of(optionPair.getFirstOption().description().text().copy().append("\n\n").append(optionPair.getSecondOption().description().text()));
	}

	@Override
	public @NotNull Component tooltip() {
		return description().text();
	}

	@Override
	public @NotNull DualController<K, V> controller() {
		return controller;
	}

	@Override
	public @NotNull StateManager<OptionPair<K, V>> stateManager() {
		return null;
	}

	@Override
	public void addEventListener(OptionEventListener<OptionPair<K, V>> optionEventListener) {
	}

	@Override
	public @NotNull Binding<OptionPair<K, V>> binding() {
		return null;
	}

	@Override
	public boolean available() {
		return optionPair.getFirstOption().available() && optionPair.getSecondOption().available();
	}

	@Override
	public void setAvailable(boolean available) {
		boolean changed = this.available != available;

		this.available = available;
		this.firstOption.setAvailable(available);
		this.secondOption.setAvailable(available);

		if (changed) {
			if (!available) {
				this.forgetPendingValue();
			}

			this.triggerListeners(!available);
		}
	}

	@Override
	public @NotNull ImmutableSet<OptionFlag> flags() {
		return flags;
	}

	@Override
	public boolean changed() {
		return !firstOption.binding().getValue().equals(firstOption.pendingValue()) || !secondOption.binding().getValue().equals(secondOption.pendingValue());
	}

	@Override
	public @NotNull OptionPair<K, V> pendingValue() {
		return null;
	}

	@Override
	public void requestSet(@NotNull OptionPair<K, V> value) {
		Validate.notNull(value, "`value` cannot be null");

		K firstOption = optionPair.getFirstOption();
		V secondOption = optionPair.getSecondOption();

		// This just doesnt work, why?
		//firstOption.requestSet(firstOption.pendingValue());
		//secondOption.requestSet(secondOption.pendingValue());

		this.triggerListeners(true);
	}

	@Override
	public boolean applyValue() {
		boolean changed = changed();
		if (changed) {
			this.firstOption.applyValue();
			this.secondOption.applyValue();
		}
		return changed;
	}

	@Override
	public void forgetPendingValue() {
		this.firstOption.forgetPendingValue();
		this.secondOption.forgetPendingValue();
	}

	@Override
	public void requestSetDefault() {
		this.firstOption.requestSetDefault();
		this.secondOption.requestSetDefault();
	}

	@Override
	public boolean isPendingValueDefault() {
		return this.firstOption.isPendingValueDefault() && this.secondOption.isPendingValueDefault();
	}

	@Override
	public void addListener(BiConsumer<Option<OptionPair<K, V>>, OptionPair<K, V>> changedListener) {
		this.listeners.add(changedListener);
	}

	private void triggerListeners(boolean bypass) {
		if (bypass || listenerTriggerDepth == 0) {
			if (listenerTriggerDepth > 10) {
				throw new IllegalStateException("Listener trigger depth exceeded 10! This means a listener triggered a listener etc etc 10 times deep. This is likely a bug in the mod using YACL!");
			}

			this.listenerTriggerDepth++;

			for (BiConsumer<Option<OptionPair<K, V>>, OptionPair<K, V>> listener : listeners) {
				try {
					listener.accept(this, optionPair);
				} catch (Exception e) {
					YACLConstants.LOGGER.error("Exception whilst triggering listener for option '%s'".formatted(name.getString()), e);
				}
			}

			this.listenerTriggerDepth--;
		}
	}

	public static <K extends Option<?>, V extends Option<?>> Builder<OptionPair<K, V>> createBuilder() {
		return new HolderOptionBuilder<>();
	}

	@ApiStatus.Internal
	public static class HolderOptionBuilder<K extends Option<?>, V extends Option<?>> implements Builder<OptionPair<K, V>>
	{
		private Component name = Component.literal("Name not specified!").withStyle(ChatFormatting.RED);

		private Function<OptionPair<K, V>, OptionDescription> descriptionFunction = pending -> OptionDescription.EMPTY;

		private Function<Option<OptionPair<K, V>>, DualController<K, V>> controlGetter;

		private OptionPair<K, V> optionPair;

		private boolean available = true;

		private boolean instant = false;

		private final Set<OptionFlag> flags = new HashSet<>();

		private final List<BiConsumer<Option<OptionPair<K, V>>, OptionPair<K, V>>> listeners = new ArrayList<>();

		@Override
		public Builder<OptionPair<K, V>> name(@NotNull Component name) {
			Validate.notNull(name, "`name` cannot be null");

			this.name = name;
			return this;
		}

		@Override
		public Builder<OptionPair<K, V>> description(@NotNull OptionDescription description) {
			return description(opt -> description);
		}

		@Override
		public Builder<OptionPair<K, V>> description(@NotNull Function<OptionPair<K, V>, OptionDescription> descriptionFunction) {
			this.descriptionFunction = descriptionFunction;
			return this;
		}

		@Override
		public Builder<OptionPair<K, V>> controller(@NotNull Function<Option<OptionPair<K, V>>, ControllerBuilder<OptionPair<K, V>>> controllerBuilder) {
			Validate.notNull(controllerBuilder, "`controllerBuilder` cannot be null");

			return customController(opt -> controllerBuilder.apply(opt).build());
		}

		@Override
		public Builder<OptionPair<K, V>> customController(@NotNull Function<Option<OptionPair<K, V>>, Controller<OptionPair<K, V>>> control) {
			Validate.notNull(control, "`control` cannot be null");
			this.controlGetter = opt -> (DualController<K, V>) control.apply(opt);
			return this;
		}

		public Builder<OptionPair<K, V>> stateManager(@NotNull StateManager<OptionPair<K, V>> stateManager) {
			return null;
		}

		@Override
		public Builder<OptionPair<K, V>> binding(@NotNull Binding<OptionPair<K, V>> binding) {
			return null;
		}

		@Override
		public Builder<OptionPair<K, V>> binding(
			@NotNull OptionPair<K, V> def,
			@NotNull Supplier<@NotNull OptionPair<K, V>> getter,
			@NotNull Consumer<@NotNull OptionPair<K, V>> setter
		) {
			return null;
		}

		public Builder<OptionPair<K, V>> optionPair(@NotNull OptionPair<K, V> optionPair) {
			Validate.notNull(optionPair, "`optionPair` cannot be null");
			this.optionPair = optionPair;
			return this;
		}

		@Override
		public Builder<OptionPair<K, V>> available(boolean available) {
			this.available = available;
			return this;
		}

		@Override
		public Builder<OptionPair<K, V>> flag(@NotNull OptionFlag... flag) {
			Validate.notNull(flag, "`flag` must not be null");

			this.flags.addAll(Arrays.asList(flag));
			return this;
		}

		@Override
		public Builder<OptionPair<K, V>> flags(@NotNull Collection<? extends OptionFlag> flags) {
			Validate.notNull(flags, "`flags` must not be null");

			this.flags.addAll(flags);
			return this;
		}

		@Override
		public Builder<OptionPair<K, V>> addListener(@NotNull OptionEventListener<OptionPair<K, V>> optionEventListener) {
			return null;
		}

		@Override
		public Builder<OptionPair<K, V>> addListeners(@NotNull Collection<OptionEventListener<OptionPair<K, V>>> collection) {
			return null;
		}

		@Override
		public Builder<OptionPair<K, V>> instant(boolean instant) {
			this.instant = instant;
			return this;
		}

		@Override
		public Builder<OptionPair<K, V>> listener(@NotNull BiConsumer<Option<OptionPair<K, V>>, OptionPair<K, V>> listener) {
			this.listeners.add(listener);
			return this;
		}

		@Override
		public Builder<OptionPair<K, V>> listeners(@NotNull Collection<BiConsumer<Option<OptionPair<K, V>>, OptionPair<K, V>>> listeners) {
			this.listeners.addAll(listeners);
			return this;
		}

		@Override
		public Option<OptionPair<K, V>> build() {
			Validate.notNull(controlGetter, "`control` must not be null when building `Option`");
			Validate.isTrue(!instant || flags.isEmpty(), "instant application does not support option flags");

			if (instant) {
				listeners.add((opt, pendingValue) -> opt.applyValue());
			}

			return new HolderOption<>(name, descriptionFunction, controlGetter, available, ImmutableSet.copyOf(flags), listeners);
		}
	}
}