package com.sukajee.chirpbe.api.controllers

import com.sukajee.chirpbe.api.dto.RegisterRequest
import com.sukajee.chirpbe.api.dto.UserDto
import com.sukajee.chirpbe.api.mappers.toUserDto
import com.sukajee.chirpbe.service.auth.AuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authSercie: AuthService) {
	
	@PostMapping("/register")
	fun register(
		@Valid @RequestBody body: RegisterRequest
	): UserDto {
		return authSercie.register(
			username = body.username,
			email = body.email,
			password = body.password
		).toUserDto()
	}
}