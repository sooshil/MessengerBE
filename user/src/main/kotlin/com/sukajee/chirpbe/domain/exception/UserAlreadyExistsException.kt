package com.sukajee.chirpbe.domain.exception

import java.lang.RuntimeException

class UserAlreadyExistsException: RuntimeException("User already exists!")
class PasswordEncodeException: RuntimeException("There was some problem encoding the password.")