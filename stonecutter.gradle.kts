val IS_CI = System.getenv("CI") == "true"

plugins {
    id("dev.kikugie.stonecutter")
	id("net.neoforged.moddev") version "2.0.147" apply false
	id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT" apply false
	id("net.fabricmc.fabric-loom-remap") version "1.18-SNAPSHOT" apply false
}

stonecutter {
	parameters {
		filters.exclude("**/*.accesswidener")

		replacements.string(current.parsed >= "1.21.11") {
			replace("ResourceLocation", "Identifier")
			replace("net.minecraft.Util", "net.minecraft.util.Util")
			replace("net.minecraft.client.renderer.RenderType", "net.minecraft.client.renderer.rendertype.RenderTypes")
			replace("RenderType.lines()", "RenderTypes.lines()")
		}

		replacements.string(current.parsed >= "26.3") {
			replace("getStructureManager()", "getStructureTemplateManager()")
			replace("datapackWorldgen()", "datapackWorldRegistries()")
			replace("WORLDGEN_REGISTRIES", "WORLD_REGISTRIES")
		}
	}
}

if (IS_CI) stonecutter active null
else stonecutter active "26.3" /* [SC] DO NOT EDIT */
