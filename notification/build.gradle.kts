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
	implementation(libs.spring.boot.starter.amqp)
	
	testImplementation(kotlin("test"))
}

tasks.test {
	useJUnitPlatform()
}