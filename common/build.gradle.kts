plugins {
	id("java-library")
	id("chirpbe.kotlin-common")
	id("org.springframework.boot")
}

group = "org.sukajee"
version = "unspecified"

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	testImplementation(kotlin("test"))
}

tasks.test {
	useJUnitPlatform()
}