package com.sukajee.chirpbe.domain.exception

import jdk.internal.joptsimple.internal.Messages.message

class UserNotFoundException() : RuntimeException(
	"User not found."
)