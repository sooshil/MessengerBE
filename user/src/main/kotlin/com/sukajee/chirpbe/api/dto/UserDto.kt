package com.sukajee.chirpbe.api.dto

import com.sukajee.chirpbe.domain.model.UserId

data class UserDto(
	val id: UserId,
	val username: String,
	val email: String,
	val hasEmailVerified: Boolean
)