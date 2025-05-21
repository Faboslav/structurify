import dev.kikugie.stonecutter.RunConfigType

plugins {
    id("dev.kikugie.stonecutter")

    id("net.neoforged.moddev") version "2.0.78" apply false
    id("fabric-loom") version "1.10-SNAPSHOT" apply false
}

stonecutter active "1.20.1" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
	group = mod.id
	ofTask("build")
}

stonecutter {
	generateRunConfigs = listOf(RunConfigType.SWITCH)
}