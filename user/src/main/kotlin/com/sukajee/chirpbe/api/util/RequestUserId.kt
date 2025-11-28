package com.sukajee.chirpbe.api.util

import com.sukajee.chirpbe.domain.exception.UnauthorizedException
import com.sukajee.chirpbe.domain.type.UserId
import org.springframework.security.core.context.SecurityContextHolder

val requestUserId: UserId
	get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
		?: throw UnauthorizedException()