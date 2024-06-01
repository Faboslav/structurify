package com.faboslav.structurify.common.events.lifecycle;

import com.faboslav.structurify.common.events.base.EventHandler;
import net.minecraft.registry.DynamicRegistryManager;

/**
 * Event related is code based on The Bumblezone/Resourceful Lib mods with permissions from the authors
 *
 * @author TelepathicGrunt
 * <a href="https://github.com/TelepathicGrunt/Bumblezone">https://github.com/TelepathicGrunt/Bumblezone</a>
 * @author ThatGravyBoat
 * <a href="https://github.com/Team-Resourceful/ResourcefulLib">https://github.com/Team-Resourceful/ResourcefulLib</a>
 */
public record TagsUpdatedEvent(DynamicRegistryManager registryManager, boolean fromPacket)
{

	public static final EventHandler<TagsUpdatedEvent> EVENT = new EventHandler<>();
}
