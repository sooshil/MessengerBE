plugins {
	id("chirpbe.spring-boot-app")
}

group = "com.sukajee"
version = "0.0.1-SNAPSHOT"
description = "Chirp Messaging App Backend"


dependencies {
	implementation(projects.chat)
	implementation(projects.notification)
	implementation(projects.user)
	implementation(projects.common)
	
	implementation(libs.spring.boot.starter.security)
	implementation(libs.spring.boot.starter.data.jpa)
	implementation(libs.spring.boot.starter.data.redis)
	runtimeOnly(libs.postgresql)
	
//	implementation("org.springframework.boot:spring-boot-starter-actuator")
//	implementation("org.springframework.boot:spring-boot-starter-amqp")
//	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	implementation("org.springframework.boot:spring-boot-starter-data-redis")
//	implementation("org.springframework.boot:spring-boot-starter-mail")
//	implementation("org.springframework.boot:spring-boot-starter-security")
//	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
//	implementation("org.springframework.boot:spring-boot-starter-web")
//	implementation("org.springframework.boot:spring-boot-starter-websocket")
//	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
//	implementation("org.jetbrains.kotlin:kotlin-reflect")
//	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
//	runtimeOnly("org.postgresql:postgresql")
//
//
//	testImplementation("org.springframework.boot:spring-boot-starter-test")
//	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
//	testImplementation("org.springframework.amqp:spring-rabbit-test")
//	testImplementation("org.springframework.security:spring-security-test")
//	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
