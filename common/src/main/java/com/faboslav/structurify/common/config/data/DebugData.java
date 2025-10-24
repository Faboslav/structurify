package com.faboslav.structurify.common.config.data;

import com.faboslav.structurify.common.Structurify;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureBiomeCheckOverview;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureBiomeCheckSample;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureFlatnessCheckOverview;
import com.faboslav.structurify.world.level.structure.checks.debug.StructureFlatnessCheckSample;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class DebugData
{
	private final Map<Long, StructureFlatnessCheckOverview> structureFlatnessCheckOverviews = new ConcurrentHashMap<>();
	private final Map<Long, Set<StructureFlatnessCheckSample>> structureFlatnessCheckSamples = new ConcurrentHashMap<>();

	private final Map<Long, StructureBiomeCheckOverview> structureBiomeCheckOverviews = new ConcurrentHashMap<>();
	private final Map<Long, Set<StructureBiomeCheckSample>> structureBiomeCheckSamples = new ConcurrentHashMap<>();

	private boolean isEnabled = false;
	private DebugMode debugMode = DebugMode.NONE;
	private SamplingMode samplingMode = SamplingMode.FINAL;

	public boolean isEnabled() {
		return this.isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public DebugMode getDebugMode() {
		return this.debugMode;
	}

	public void setDebugMode(DebugMode debugMode) {
		this.debugMode = debugMode;
	}

	public SamplingMode getSamplingMode() {
		return this.samplingMode;
	}

	public void setSamplingMode(SamplingMode samplingMode) {
		this.samplingMode = samplingMode;
	}

	public void addStructureFlatnessCheckSample(
		Long structureKey,
		StructureFlatnessCheckSample structureFlatnessCheckSample
	) {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureFlatnessCheckSamples) {
			if (!this.structureFlatnessCheckSamples.containsKey(structureKey)) {
				this.structureFlatnessCheckSamples.put(structureKey, ConcurrentHashMap.newKeySet());
			}

			this.structureFlatnessCheckSamples.get(structureKey).add(structureFlatnessCheckSample);
		}
	}

	public void removeStructureFlatnessCheckSamples(Long structureKey) {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		this.structureFlatnessCheckSamples.remove(structureKey);
	}

	public void clearStructureFlatnessCheckSamples() {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureFlatnessCheckSamples) {
			this.structureFlatnessCheckSamples.clear();
		}
	}

	public Map<Long, Set<StructureFlatnessCheckSample>> getStructureFlatnessCheckSamples() {
		return this.structureFlatnessCheckSamples;
	}

	public void clearStructureFlatnessCheckOverviews() {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureFlatnessCheckOverviews) {
			this.structureFlatnessCheckOverviews.clear();
		}
	}

	public void addStructureFlatnessCheckInfo(
		Long structureKey,
		StructureFlatnessCheckOverview structureFlatnessCheckInfo
	) {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureFlatnessCheckOverviews) {
			this.structureFlatnessCheckOverviews.putIfAbsent(structureKey, structureFlatnessCheckInfo);
		}
	}

	public void removeStructureFlatnessCheckInfo(Long structureKey) {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		this.structureFlatnessCheckOverviews.remove(structureKey);
	}

	public Map<Long, StructureFlatnessCheckOverview> getStructureFlatnessCheckOverviews() {
		return this.structureFlatnessCheckOverviews;
	}

	public void addStructureBiomeCheckOverview(
		Long structureKey,
		StructureBiomeCheckOverview structureBiomeCheckOverview
	) {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureBiomeCheckOverviews) {
			this.structureBiomeCheckOverviews.putIfAbsent(structureKey, structureBiomeCheckOverview);
		}
	}

	public void removeStructureBiomeCheckOverview(Long structureKey) {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		this.structureBiomeCheckOverviews.remove(structureKey);
	}

	public void clearStructureBiomeCheckOverviews() {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureBiomeCheckOverviews) {
			this.structureBiomeCheckOverviews.clear();
		}
	}

	public Map<Long, StructureBiomeCheckOverview> getStructureBiomeCheckOverviews() {
		return this.structureBiomeCheckOverviews;
	}

	public void addStructureBiomeCheckSample(Long structureKey, StructureBiomeCheckSample structureBiomeCheckSample) {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureBiomeCheckSamples) {
			if (!this.structureBiomeCheckSamples.containsKey(structureKey)) {
				this.structureBiomeCheckSamples.put(structureKey, ConcurrentHashMap.newKeySet());
			}

			this.structureBiomeCheckSamples.get(structureKey).add(structureBiomeCheckSample);
		}
	}

	public void removeStructureBiomeCheckSamples(Long structureKey) {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		this.structureBiomeCheckSamples.remove(structureKey);
	}

	public void clearStructureBiomeCheckSamples() {
		if (!Structurify.getConfig().getDebugData().isEnabled()) {
			return;
		}

		synchronized (this.structureBiomeCheckSamples) {
			this.structureBiomeCheckSamples.clear();
		}
	}

	public Map<Long, Set<StructureBiomeCheckSample>> getStructureBiomeCheckSamples() {
		return this.structureBiomeCheckSamples;
	}

	public enum DebugMode
	{
		NONE("none"),
		BIOME("biome"),
		FLATNESS("flatness"),
		OVERLAP("overlap");

		private final String name;

		DebugMode(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static Stream<String> getNames() {
			return Arrays.stream(values()).map(DebugMode::getName);
		}
	}

	public enum SamplingMode
	{
		MINIMAL("minimal"),
		MERGED_SAMPLES("merged_samples"),
		FINAL("final");

		private final String name;

		SamplingMode(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public static Stream<String> getNames() {
			return Arrays.stream(values()).map(SamplingMode::getName);
		}
	}
}
