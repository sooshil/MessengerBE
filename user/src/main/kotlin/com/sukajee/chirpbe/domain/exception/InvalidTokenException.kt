package com.sukajee.chirpbe.domain.exception

class InvalidTokenException(
	override val message: String?
) : RuntimeException(message ?: "Invalid token")