package com.sukajee.chirpbe.domain.exception

class SamePasswordException() : RuntimeException(
	"The new and old password can't be same."
)