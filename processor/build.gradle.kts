plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.pkl-lang:pkl-codegen-kotlin:0.30.2")
    implementation("org.pkl-lang:pkl-config-kotlin:0.30.2")
    implementation("org.pkl-lang:pkl-config-java:0.30.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0-RC")
}