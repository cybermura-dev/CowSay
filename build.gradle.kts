plugins {
    id("java")
    kotlin("jvm")
}

group = "ru.takeshiko.minecraft.cowsay"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots") {
        name = "bungeecord-repo"
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compileOnly("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT")
    compileOnly(dependencyNotation = "net.md-5:bungeecord-api:1.16-R0.4")
}

kotlin {
    jvmToolchain(8)
}

tasks.jar {
    archiveFileName.set("${project.name}-${project.version}.jar")
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    }) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}