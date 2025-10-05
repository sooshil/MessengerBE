package com.sukajee.chirpbe.domain.exception

import jdk.internal.joptsimple.internal.Messages.message

class InvalidCredentialsException() : RuntimeException(
	"The entered credentials are not valid."
)