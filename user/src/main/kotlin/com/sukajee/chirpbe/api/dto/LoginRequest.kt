package com.sukajee.chirpbe.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
	@field:NotBlank(message = "Email must not be blank.")
	@field:Email(message = "Must be a valid email address.")
	@JsonProperty("email")
	val email: String,
	
	@field:NotBlank(message = "Password must not be blank.")
	@JsonProperty("password")
	val password: String
)