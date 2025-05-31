plugins {
	`multiloader-loader`
	id("net.neoforged.moddev.legacyforge")
	id("dev.kikugie.j52j") version "2.0"
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
	annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")

	"io.github.llamalad7:mixinextras-common:0.4.1".let {
		compileOnly(it)
		annotationProcessor(it)
	}

	"io.github.llamalad7:mixinextras-forge:0.4.1".let {
		implementation(it)
		jarJar(it)
	}

	// Required dependencies
	modImplementation("dev.isxander:yet-another-config-lib:${commonMod.dep("yacl")}-forge")

	// Global Packs
	commonMod.depOrNull("global_packs")?.let { globalPacksVersion ->
		modImplementation(commonMod.modrinth("globalpacks", "1.16.2_forge")) { isTransitive = false }
	}

	// Open Loader
	commonMod.depOrNull("open_loader")?.let { openLoaderVersion ->
		modImplementation(
			group = "net.darkhax.openloader",
			name = "OpenLoader-Forge-${commonMod.mc}",
			version = openLoaderVersion
		) { isTransitive = false }
	}

	// Yungs Api
	commonMod.depOrNull("yungs_api_minecraft")?.let { yungsApiMcVersion ->
		commonMod.depOrNull("yungs_api")?.let { yungsApiVersion ->
			modImplementation("com.yungnickyoung.minecraft.yungsapi:YungsApi:$yungsApiMcVersion-Forge-$yungsApiVersion") { isTransitive = false }
		}
	}

	// Repurposed Structures
	commonMod.depOrNull("repurposed_structures")?.let { repurposedStructuresVersion ->
		modImplementation(
			commonMod.modrinth(
				"repurposed-structures-forge",
				"${repurposedStructuresVersion}-forge"
			)
		) { isTransitive = false }
	}

	// Structure Gel Api
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

	jar {
		finalizedBy("reobfJar")
		manifest {
			attributes(
				mapOf(
					"MixinConfigs" to "${mod.id}-common.mixins.json,${mod.id}-forge.mixins.json"
				)
			)
		}
	}
}