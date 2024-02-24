plugins {
    java
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.eclipse.jgit", "org.eclipse.jgit", "6.3.0.202209071007-r")
}