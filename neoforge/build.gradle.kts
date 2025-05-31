plugins {
	`multiloader-loader`
	id("net.neoforged.moddev")
	id("dev.kikugie.j52j") version "2.0"
}

neoForge {
	enable {
		version = commonMod.dep("neoforge")
	}
}

dependencies {
	// Required dependencies
	implementation("dev.isxander:yet-another-config-lib:${commonMod.dep("yacl")}-neoforge")

	// Global Packs
	commonMod.depOrNull("global_packs")?.let { globalPacksVersion ->
		implementation(commonMod.modrinth("globalpacks", globalPacksVersion)) { isTransitive = false }
	}

	// Open Loader
	commonMod.depOrNull("open_loader")?.let { openLoaderVersion ->
		println("including")
		if (commonMod.mc == "1.21.1") {
			implementation(
				group = "net.darkhax.openloader",
				name = "openloader-neoforge-${commonMod.mc}",
				version = openLoaderVersion
			)
		} else {
			implementation(
				group = "net.darkhax.openloader",
				name = "OpenLoader-NeoForge-${commonMod.mc}",
				version = openLoaderVersion
			)
		}
	}

	// Yungs api
	commonMod.depOrNull("yungs_api_minecraft")?.let { yungsApiMcVersion ->
		commonMod.depOrNull("yungs_api")?.let { yungsApiVersion ->
			implementation("com.yungnickyoung.minecraft.yungsapi:YungsApi:$yungsApiMcVersion-NeoForge-$yungsApiVersion") {
				isTransitive = false
			}
		}
	}

	// Repurposed Structures
	commonMod.depOrNull("repurposed_structures")?.let { repurposedStructuresVersion ->
		implementation("com.telepathicgrunt:RepurposedStructures:${repurposedStructuresVersion}-neoforge")
	}
}

neoForge {
	accessTransformers.from(project.file("../../src/main/resources/META-INF/accesstransformer.cfg").absolutePath)

	runs {
		register("client") {
			client()
			ideName = "NeoForge Client (${project.path})"
		}
		register("server") {
			server()
			ideName = "NeoForge Server (${project.path})"
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