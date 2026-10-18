plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("plugin.spring") version "2.4.20"
    kotlin("plugin.jpa") version "2.4.20"
    id("org.springframework.boot") version "4.1.1"
    id("com.vaadin") version "25.3.2"
}

group = "myfinanceregister"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
    implementation(platform("com.vaadin:vaadin-bom:25.3.2"))
    implementation("com.vaadin:vaadin-spring-boot-starter:25.3.2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    developmentOnly("com.vaadin:vaadin-dev")
    runtimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
