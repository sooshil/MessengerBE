package com.sukajee.chirpbe.domain.model

import com.sukajee.chirpbe.domain.type.UserId

data class User(
	val id: UserId,
	val username: String,
	val email: String,
	val hasEmailVerified: Boolean
)