package com.sukajee.chirpbe.infra.database.mappers

import com.sukajee.chirpbe.domain.model.User
import com.sukajee.chirpbe.infra.database.entities.UserEntity

fun UserEntity.toUser() : User {
	return User(
		id = id!!,
		username = username,
		email = email,
		hasEmailVerified = hasEmailVerified
	)
}