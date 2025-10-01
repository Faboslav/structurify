package com.faboslav.structurify.common.config.data;

import java.util.Arrays;
import java.util.stream.Stream;

public final class DebugData
{
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

	public enum DebugMode {
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

	public enum SamplingMode {
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
