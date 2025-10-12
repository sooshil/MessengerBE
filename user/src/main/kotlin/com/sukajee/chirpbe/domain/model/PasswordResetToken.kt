package com.sukajee.chirpbe.domain.model

data class PasswordResetToken(
	val id: Long,
	val token: String,
	val user: User
)