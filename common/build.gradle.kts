plugins {
	id("java-library")
	id("chirpbe.kotlin-common")
}

group = "org.sukajee"
version = "unspecified"

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	api(libs.kotlin.reflect)
	api(libs.jackson.module.kotlin)
	
	implementation(libs.spring.boot.starter.amqp)
	testImplementation(kotlin("test"))
}

tasks.test {
	useJUnitPlatform()
}