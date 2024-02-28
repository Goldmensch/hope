plugins {
    id("java")
    id("me.champeau.jmh") version "0.7.2"
}

group = "io.github.madethoughts.hope"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":nbt"))
    implementation("me.nullicorn", "Nedit", "2.2.0")
    implementation("net.kyori", "adventure-nbt", "4.16.0")
    implementation("io.github.jglrxavpok.hephaistos", "common", "2.2.0")

    implementation("it.unimi.dsi:fastutil:8.5.13")
}

val ENABLE_PREVIEW = listOf(
        "--enable-preview"
)

jmh {
//    includes = listOf("DeserializerBenchmark")
    jvmArgs.set(ENABLE_PREVIEW)
    profilers.set(listOf(
            "async:output=jfr;dir=${project.layout.buildDirectory.asFile.get()}/results/async",
            "perfasm",
            "perf"
    )
    )
    resultFormat = "json"

    fork = 1
    // fork = 2
    iterations = 4
    warmupIterations = 3
    warmupForks = 0
}



java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    compileJava {
        options.compilerArgs = ENABLE_PREVIEW
        modularity.inferModulePath.set(true)
    }

    test {
        useJUnitPlatform()
    }

    compileJmhJava {
        options.compilerArgs = ENABLE_PREVIEW
    }
}
