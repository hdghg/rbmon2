plugins {
    java
    id("org.springframework.boot") version "3.3.0"
    id("org.graalvm.buildtools.native") version "0.10.1"
}

group = "com.github.hdghg"
version = "0.3.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
        vendor = JvmVendorSpec.GRAAL_VM
    }
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven(url = "https://m2.dv8tion.net/releases")
}

dependencies {
    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("com.h2database:h2")
    implementation("org.liquibase:liquibase-core")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("net.dv8tion:JDA:4.4.1_353")
    implementation("org.jsoup:jsoup:1.15.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("-march=x86-64-v2")
        }
    }
}