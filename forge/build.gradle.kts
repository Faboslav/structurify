plugins {
	`multiloader-loader`
	id("net.neoforged.moddev.legacyforge")
	id("dev.kikugie.j52j") version "2.0"
}

stonecutter {
	const("curios", commonMod.depOrNull("curios") != null)
}

mixin {
	add(sourceSets.main.get(), "${mod.id}.refmap.json")

	config("${mod.id}-common.mixins.json")
	config("${mod.id}-forge.mixins.json")
}

legacyForge {
	enable {
		forgeVersion = "${mod.mc}-${commonMod.dep("forge")}"
	}
}

dependencies {
	compileOnly("org.jetbrains:annotations:24.1.0")
	compileOnly("io.github.llamalad7:mixinextras-common:0.4.1")
	annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")

	implementation("io.github.llamalad7:mixinextras-forge:0.4.1") {
		artifact {
			name = "mixinextras-forge"
			extension = "jar"
			type = "jar"
		}
	}

	// Required dependencies
	modImplementation("dev.isxander:yet-another-config-lib:${commonMod.dep("yacl")}-forge")

	// Compat dependencies
	commonMod.depOrNull("open_loader")?.let { openLoaderVersion ->
		modImplementation(commonMod.modrinth("open-loader", openLoaderVersion)) { isTransitive = false }
	}

	commonMod.depOrNull("repurposed_structures")?.let { repurposedStructuresVersion ->
		modImplementation(commonMod.modrinth("repurposed-structures-forge", "${repurposedStructuresVersion}-forge")) { isTransitive = false }
	}

	commonMod.depOrNull("structure_gel_api")?.let { structureGelApiVersion ->
		modImplementation(commonMod.modrinth("structure-gel-api", structureGelApiVersion)) { isTransitive = false }
	}

	// For debugging
	// modImplementation(modrinth("blue-skies", "1.3.31")) { transitive = false }
}

legacyForge {
	accessTransformers.from(project.file("../../src/main/resources/META-INF/accesstransformer.cfg").absolutePath)

	runs {
		register("client") {
			client()
			ideName = "Forge Client (${project.path})"
		}
		register("server") {
			server()
			ideName = "Forge Server (${project.path})"
		}
	}

	parchment {
		commonMod.depOrNull("parchment")?.let {
			mappingsVersion = it
			minecraftVersion = commonMod.mc
		}
	}

	mods {
		register(commonMod.id) {
			sourceSet(sourceSets.main.get())
		}
	}
}

sourceSets.main {
	resources.srcDir("src/generated/resources")
}

tasks {
	processResources {
		exclude("${mod.id}.accesswidener")
	}
}