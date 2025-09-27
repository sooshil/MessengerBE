package com.sukajee.chirpbe.api.mappers

import com.sukajee.chirpbe.api.dto.AuthenticatedUserDto
import com.sukajee.chirpbe.api.dto.UserDto
import com.sukajee.chirpbe.domain.model.AuthenticatedUser
import com.sukajee.chirpbe.domain.model.User

fun AuthenticatedUser.toAuthenticatedUserDto(): AuthenticatedUserDto {
	return AuthenticatedUserDto(
		user = user.toUserDto(),
		accessToken = accessToken,
		refreshToken = refreshToken
	)
}

fun User.toUserDto(): UserDto {
	return UserDto(
		id = id,
		username = username,
		email = email,
		hasEmailVerified = hasEmailVerified
	)
}