package com.sukajee.chirpbe.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.sukajee.chirpbe.api.util.Password
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length

data class ChangePasswordRequest(
	@field:NotBlank
	@JsonProperty("oldPassword")
	val oldPassword: String,
	
	@field:Password //Our own annotation to validate password.
	@JsonProperty("newPassword")
	val newPassword: String
)
