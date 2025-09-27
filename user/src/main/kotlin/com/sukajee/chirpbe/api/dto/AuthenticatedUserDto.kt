package com.sukajee.chirpbe.api.dto

data class AuthenticatedUserDto(
	val user: UserDto,
	val accessToken: String,
	val refreshToken: String
)
