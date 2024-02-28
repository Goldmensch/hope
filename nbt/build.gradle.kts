plugins {
    id("java")
}

group = "io.github.madethoughts.hope"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    implementation("net.kyori", "adventure-nbt", "4.16.0")

    testImplementation("it.unimi.dsi:fastutil:8.5.13")
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

val ENABLE_PREVIEW = listOf(
        "--enable-preview"
)

tasks {
    compileJava {
        options.compilerArgs = ENABLE_PREVIEW
        modularity.inferModulePath.set(true)
    }

    compileTestJava {
        options.compilerArgs = ENABLE_PREVIEW
    }

    test {
        useJUnitPlatform()
        jvmArgs = ENABLE_PREVIEW
    }
}