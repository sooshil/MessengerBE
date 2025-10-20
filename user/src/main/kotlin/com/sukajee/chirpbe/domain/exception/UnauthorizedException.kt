package com.sukajee.chirpbe.domain.exception

import java.lang.RuntimeException

class UnauthorizedException: RuntimeException("Missing auth details.")