package com.sukajee.chirpbe.domain.exception

import java.lang.RuntimeException

class RateLimitException(
	val resetsInSeconds: Long
): RuntimeException("Rate limit exceed. Please try again in $resetsInSeconds seconds.")