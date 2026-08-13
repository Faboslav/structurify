package com.faboslav.structurify.common.registry;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.common.mixin.structure.StructureTemplatePoolMixin;
import com.faboslav.structurify.common.mixin.structure.jigsaw.JigsawStructureAccessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.util.*;

public final class StructurifyTemplatePoolProvider
{
	private static final Map<String, Map<String, Integer>> templatePoolElementWeights = new TreeMap<>();
	private static final Map<String, Set<String>> structureTemplatePoolIds = new TreeMap<>();

	@Nullable
	public static String getStructurePoolElementLocation(StructurePoolElement structurePoolElement) {
		var serializationContext = StructurifyRegistryManagerProvider.getSerializationContext();

		if (serializationContext == null) {
			return null;
		}

		return StructurePoolElement.CODEC.encodeStart(serializationContext, structurePoolElement)
			.result()
			.filter(JsonElement::isJsonObject)
			.map(JsonElement::getAsJsonObject)
			.map(json -> json.get("location"))
			.filter(Objects::nonNull)
			.map(JsonElement::getAsString)
			.orElse(null);
	}

	public static Map<String, Map<String, Integer>> getStructureTemplatePoolElementsWithWeight() {
		var structureTemplatePoolRegistry = StructurifyRegistryManagerProvider.getStructureTemplatePoolRegistry();

		if (structureTemplatePoolRegistry == null) {
			return Collections.emptyMap();
		}

		if (templatePoolElementWeights.isEmpty()) {
			Map<String, Map<String, Integer>> structureTemplatePools = new HashMap<>();

			for (var structureTemplatePoolReference : structureTemplatePoolRegistry.listElements().toList()) {
				var structureTemplatePoolId = structureTemplatePoolReference.key()/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/.toString();
				var structureTemplatePool = structureTemplatePoolReference.value();
				var structureTemplatePoolElements = ((StructureTemplatePoolMixin) structureTemplatePool).getRawTemplates();

				Map<String, Integer> structureTemplatePoolElementsWithWeight = new HashMap<>();

				for (var structureTemplatePoolElement : structureTemplatePoolElements) {
					var structureTemplatePoolElementId = getStructurePoolElementLocation(structureTemplatePoolElement.getFirst());

					if (structureTemplatePoolElementId == null) {
						continue;
					}

					structureTemplatePoolElementsWithWeight.put(
						structureTemplatePoolElementId,
						structureTemplatePoolElement.getSecond()
					);
				}

				structureTemplatePools.put(structureTemplatePoolId, structureTemplatePoolElementsWithWeight);
			}


			templatePoolElementWeights.putAll(structureTemplatePools);
		}

		return templatePoolElementWeights;
	}

	public static Set<String> getStructureTemplatePoolIdsForStructure(String structureId) {
		if (!structureTemplatePoolIds.containsKey(structureId)) {
			var resourcePackRepository = StructurifyResourcePackProvider.getResourcePackRepository();

			try (var resourceManager = new MultiPackResourceManager(
				PackType.SERVER_DATA,
				resourcePackRepository.openAllSelected()
			)) {
				var structureTemplatePools = loadStructureTemplatePoolsForStructure(resourceManager, structureId);
				structureTemplatePoolIds.put(structureId, structureTemplatePools);
			}
		}

		return structureTemplatePoolIds.get(structureId);
	}

	private static Set<String> loadStructureTemplatePoolsForStructure(ResourceManager resourceManager, String structureId) {
		var structureRegistry = StructurifyRegistryManagerProvider.getStructureRegistry();

		if (structureRegistry == null) {
			return Set.of();
		}

		Optional<Holder.Reference<Structure>> structure = structureRegistry.get(ResourceKey.create(Registries.STRUCTURE, Structurify.makeNamespacedId(structureId)));

		if(structure.isEmpty()) {
			return Set.of();
		}

		return loadStructureTemplatePoolsForStructure(resourceManager, structure.get().value(), structureId);
	}

	private static Set<String> loadStructureTemplatePoolsForStructure(ResourceManager resourceManager, Structure structure, String structureId) {
		if (!(structure instanceof JigsawStructure jigsawStructure)) {
			return Set.of();
		}

		var startPool = ((JigsawStructureAccessor) (Object) jigsawStructure).structurify$getOriginalStartPool().unwrapKey().orElse(null);

		if (startPool == null) {
			return Set.of();
		}

		try {
			Set<Identifier> structureTemplatePools = collectStructureTemplatePools(resourceManager, startPool/*? if >= 1.21.11 {*/.identifier()/*?} else {*//*.location()*//*?}*/);

			if(structureTemplatePools.isEmpty()) {
				return Set.of();
			}

			return structureTemplatePools.stream()
				.map(Identifier::toString)
				.collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
		} catch(Throwable e) {
			// TODO log error
			return Set.of();
		}
	}

	private static Set<Identifier> collectStructureTemplatePools(

		ResourceManager resourceManager,
		Identifier startPoolId

	) throws IOException {

		Set<Identifier> usedTemplatePools = new LinkedHashSet<>();
		collectStructureTemplatePools(resourceManager, startPoolId, usedTemplatePools);
		return Set.copyOf(usedTemplatePools);

	}

	private static void collectStructureTemplatePools(
		ResourceManager resourceManager,
		Identifier templatePoolId,
		Set<Identifier> usedTemplatePools
	) throws IOException {

		if (templatePoolId.equals(Structurify.makeNamespacedId("minecraft:empty")) || !usedTemplatePools.add(templatePoolId)) {
			return;
		}

		Set<Identifier> elementIds = loadTemplatePoolElementIds(resourceManager, templatePoolId, usedTemplatePools);

		for (Identifier elementId : elementIds) {
			var structureTemplateNbt = loadStructureTemplateNbt(resourceManager, elementId);

			if (structureTemplateNbt.isEmpty()) {
				continue;
			}

			collectTemplatePoolsUsedInNbtJigsawBlocks(resourceManager, structureTemplateNbt.get(), usedTemplatePools);
		}

	}

	private static Set<Identifier> loadTemplatePoolElementIds(
		ResourceManager resourceManager,
		Identifier templatePoolId,
		Set<Identifier> usedTemplatePools
	) throws IOException {
		Identifier templatePoolJsonId = templatePoolId.withPath(path -> "worldgen/template_pool/" + path + ".json");
		Optional<Resource> templatePoolResource = resourceManager.getResource(templatePoolJsonId);

		if (templatePoolResource.isEmpty()) {
			return Set.of();
		}

		Set<Identifier> elementIds = new LinkedHashSet<>();

		try (var reader = templatePoolResource.get().openAsReader()) {
			JsonObject templatePoolJson = GsonHelper.parse(reader);
			String fallback = GsonHelper.getAsString(templatePoolJson, "fallback", "");
			if (!fallback.isBlank() && !"minecraft:empty".equals(fallback)) {
				collectStructureTemplatePools(resourceManager, Structurify.makeNamespacedId(fallback), usedTemplatePools);
			}
			if (!templatePoolJson.has("elements")) {
				return elementIds;
			}
			for (JsonElement element : templatePoolJson.getAsJsonArray("elements")) {
				JsonObject weightedElementJson = element.getAsJsonObject();
				if (!weightedElementJson.has("element")) {
					continue;
				}
				collectTemplatePoolElementIdsFromJson(weightedElementJson.getAsJsonObject("element"), elementIds);
			}
		}

		return elementIds;

	}

	private static void collectTemplatePoolElementIdsFromJson(
		JsonObject elementJson,
		Set<Identifier> elementIds
	) {

		String elementType = GsonHelper.getAsString(elementJson, "element_type", "");
		if ("minecraft:single_pool_element".equals(elementType) || "minecraft:legacy_single_pool_element".equals(elementType)) {
			String location = GsonHelper.getAsString(elementJson, "location", "");
			if (!location.isBlank()) {
				elementIds.add(Structurify.makeNamespacedId(location));
			}
			return;
		}
		if (!"minecraft:list_pool_element".equals(elementType) || !elementJson.has("elements")) {
			return;
		}
		for (JsonElement childElement : elementJson.getAsJsonArray("elements")) {
			collectTemplatePoolElementIdsFromJson(childElement.getAsJsonObject(), elementIds);
		}
	}

	private static Optional<CompoundTag> loadStructureTemplateNbt(ResourceManager resourceManager, Identifier templateId) throws IOException {
		//? if >= 1.21.1 {
		String structureDirectory = "structure";
		//?} else {
		/*String structureDirectory = "structures";
		*///?}
		
		Identifier structureNbtId = templateId.withPath(path -> structureDirectory + "/" + path + ".nbt");
		Optional<Resource> structureNbtResource = resourceManager.getResource(structureNbtId);

		if (structureNbtResource.isEmpty()) {
			return Optional.empty();
		}

		try (var inputStream = structureNbtResource.get().open()) {
			//? if >= 1.21 {
			return Optional.of(NbtIo.readCompressed(inputStream, NbtAccounter.unlimitedHeap()));
			//?} else {
			/*return Optional.of(NbtIo.readCompressed(inputStream));
			*///?}
		}
	}

	private static void collectTemplatePoolsUsedInNbtJigsawBlocks(
		ResourceManager resourceManager,
		CompoundTag structureTemplateNbt,
		Set<Identifier> usedTemplatePools
	) throws IOException {
		//? if >= 1.21.5 {
		ListTag blocks = structureTemplateNbt.getList("blocks").orElse(new ListTag());
		//?} else {
		/*ListTag blocks = structureTemplateNbt.getList("blocks", 10);
		 *///?}

		for (int i = 0; i < blocks.size(); i++) {
			var block = blocks.getCompound(i);

			if (block.isEmpty()) {
				continue;
			}

			//? if >= 1.21.5 {
			CompoundTag blockNbt = block.get().getCompound("nbt").orElse(null);
			//?} else {
		/*if (!block.contains("nbt", 10)) {
			continue;
		}

		CompoundTag blockNbt = block.getCompound("nbt");
		*///?}

			if (blockNbt == null) {
				continue;
			}

			//? if >= 1.21.5 {
			String id = blockNbt.getString("id").orElse("");
			//?} else {
			/*String id = blockNbt.getString("id");
			 *///?}

			if (!"minecraft:jigsaw".equals(id)) {
				continue;
			}

			//? if >= 1.21.5 {
			String pool = blockNbt.getString("pool").orElse("");
			//?} else {
			/*String pool = blockNbt.getString("pool");
			 *///?}

			if (pool.isBlank()) {
				continue;
			}

			Identifier poolId = Structurify.makeNamespacedId(pool);

			if (!poolId.equals(Structurify.makeNamespacedId("minecraft:empty"))) {
				collectStructureTemplatePools(resourceManager, poolId, usedTemplatePools);
			}
		}
	}
}