import io.freefair.gradle.plugins.aspectj.AjcAction
import java.util.*

plugins {
    kotlin("plugin.spring") version "2.1.20"
    id("com.google.devtools.ksp") version "2.1.20+"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.aspectj.post-compile-weaving") version "8.13"
    jacoco
}

group = "kiss"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    implementation("com.github.loki4j:loki-logback-appender:1.6.0")
//    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // jackson yaml
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.apache.commons:commons-lang3:3.18.0")
    implementation("io.minio:minio:latest.release")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("org.apache.poi:poi:5.4.1")
    implementation("org.apache.poi:poi-ooxml:5.4.1")

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito")
    }
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.aspectj:aspectjrt:1.9.21.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter:latest.release")
    ksp("org.babyfish.jimmer:jimmer-ksp:latest.release")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
    }
    runtimeOnly("io.netty:netty-resolver-dns-native-macos::osx-aarch_64")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.larksuite.oapi:oapi-sdk:2.4.14")
    implementation("com.google.genai:google-genai:1.7.0")
    implementation("io.github.smiley4:schema-kenerator-core:2.2.0")
    implementation("io.github.smiley4:schema-kenerator-jsonschema:2.2.0")
    implementation("io.github.smiley4:schema-kenerator-reflection:2.2.0")

    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.mockk:mockk:1.13.17")
    testImplementation("org.testcontainers:minio:1.21.0")
    implementation("org.redisson:redisson-spring-boot-starter:3.50.0")
    testImplementation("com.redis:testcontainers-redis:2.2.4")
}

configurations.implementation {
    exclude(group = "commons-logging", module = "commons-logging")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.compileTestKotlin {
    configure<AjcAction> {
        options {
            aspectpath.setFrom(sourceSets.main.get().output)
        }
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run

    val envFile = rootProject.file(".env")
    if (!envFile.exists()) {
        throw GradleException("请确保项目根目录下存在.env文件")
    }
    val properties = Properties()
    envFile.reader().use { properties.load(it) }
    properties.forEach {
        val key = it.key.toString()
        val value = it.value
        environment(key, value)
    }
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}
