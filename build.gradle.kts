import io.freefair.gradle.plugins.aspectj.AjcAction

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.spring") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21+"
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
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    // jackson yaml
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.minio:minio:8.5.17")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.alipay.sdk:alipay-sdk-java:4.40.186.ALL")

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
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.larksuite.oapi:oapi-sdk:2.4.14")

    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.mockk:mockk:1.13.17")
    testImplementation("org.testcontainers:minio:1.21.0")
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
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}
