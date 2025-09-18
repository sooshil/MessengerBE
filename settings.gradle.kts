pluginManagement {
	repositories {
		maven { url = uri("https://repo.spring.io/snapshot") }
		gradlePluginPortal()
	}
}
rootProject.name = "chirpbe"
include("app")
include("user")
include("chat")
include("notification")
include("common")