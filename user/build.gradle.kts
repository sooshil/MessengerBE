plugins {
	id("java-library")
	id("chirpbe.spring-boot-service")
	kotlin("plugin.jpa")
}

group = "org.sukajee"
version = "unspecified"

repositories {
    mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	implementation(projects.common)
	implementation(libs.spring.boot.starter.security)
	implementation(libs.spring.boot.starter.validation)
	implementation(libs.spring.boot.starter.data.jpa)
	runtimeOnly(libs.postgresql)
	
	implementation(libs.jwt.api)
	runtimeOnly(libs.jwt.impl)
	runtimeOnly(libs.jwt.jackson)
	
	testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}