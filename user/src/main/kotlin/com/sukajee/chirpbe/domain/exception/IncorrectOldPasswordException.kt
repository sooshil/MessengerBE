package com.sukajee.chirpbe.domain.exception

class IncorrectOldPasswordException() : RuntimeException(
	"The old password provided was incorrect."
)