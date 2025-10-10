package com.sukajee.chirpbe.infra.database.mappers

import com.sukajee.chirpbe.domain.model.EmailVerificationToken
import com.sukajee.chirpbe.infra.database.entities.EmailVerificationTokenEntity

fun EmailVerificationTokenEntity.toEmailVerificationToken(): EmailVerificationToken {
	return EmailVerificationToken(
		id = id,
		token = token,
		user = user.toUser()
	)
}