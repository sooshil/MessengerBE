package com.sukajee.chirpbe.domain.exception

import java.lang.RuntimeException

class UserAlreadyExistsException: RuntimeException("A user with this email or username already exists!")
