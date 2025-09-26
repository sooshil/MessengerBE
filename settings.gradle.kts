pluginManagement {
	includeBuild("build-logic")
	repositories {
		maven { url = uri("https://repo.spring.io/milestone") }
		maven { url = uri("https://repo.spring.io/snapshot") }
		gradlePluginPortal()
	}
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "chirpbe"

include("app")
include("user")
include("chat")
include("notification")
include("common")