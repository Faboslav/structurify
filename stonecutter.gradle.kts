val IS_CI = System.getenv("CI") == "true"

plugins {
    id("dev.kikugie.stonecutter")
	id("net.neoforged.moddev") version "2.0.115" apply false
	id("fabric-loom") version "1.14-SNAPSHOT" apply false
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
	}
}

if (IS_CI) stonecutter active null
else stonecutter active "1.21.11" /* [SC] DO NOT EDIT */
