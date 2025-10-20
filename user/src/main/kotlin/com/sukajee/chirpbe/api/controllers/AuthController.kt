package com.sukajee.chirpbe.api.controllers

import com.sukajee.chirpbe.api.config.IpRateLimit
import com.sukajee.chirpbe.api.dto.AuthenticatedUserDto
import com.sukajee.chirpbe.api.dto.ChangePasswordRequest
import com.sukajee.chirpbe.api.dto.ForgotPasswordRequest
import com.sukajee.chirpbe.api.dto.LoginRequest
import com.sukajee.chirpbe.api.dto.RefreshRequest
import com.sukajee.chirpbe.api.dto.RegisterRequest
import com.sukajee.chirpbe.api.dto.ResetPasswordRequest
import com.sukajee.chirpbe.api.dto.UserDto
import com.sukajee.chirpbe.api.mappers.toAuthenticatedUserDto
import com.sukajee.chirpbe.api.mappers.toUserDto
import com.sukajee.chirpbe.infra.rate_limiting.EmailRateLimiter
import com.sukajee.chirpbe.service.AuthService
import com.sukajee.chirpbe.service.EmailVerificationService
import com.sukajee.chirpbe.service.PasswordResetService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
	private val authService: AuthService,
	private val emailVerificationService: EmailVerificationService,
	private val passwordResetService: PasswordResetService,
	private val emailRateLimiter: EmailRateLimiter
) {
	
	@PostMapping("/register")
	@IpRateLimit(requests = 10, duration = 1L, unit = TimeUnit.HOURS)
	fun register(
		@Valid @RequestBody body: RegisterRequest
	): UserDto {
		return authService.register(
			username = body.username,
			email = body.email,
			password = body.password
		).toUserDto()
	}
	
	@PostMapping("/login")
	@IpRateLimit(requests = 10, duration = 1L, unit = TimeUnit.HOURS)
	fun login(
		@Valid @RequestBody body: LoginRequest
	): AuthenticatedUserDto {
		return authService.login(
			email = body.email,
			password = body.password
		).toAuthenticatedUserDto()
	}
	
	@PostMapping("/refresh")
	fun refresh(
		@RequestBody body: RefreshRequest
	): AuthenticatedUserDto {
		return authService
			.refresh(refreshToken = body.refreshToken)
			.toAuthenticatedUserDto()
	}
	
	@PostMapping("/logout")
	fun logout(@RequestBody body: RefreshRequest) {
		authService.logout(refreshToken = body.refreshToken)
	}
	
	@PostMapping("/resend-verification-email")
	@IpRateLimit(requests = 10, duration = 1L, unit = TimeUnit.HOURS)
	fun resendVerificationEmail(
		@Valid @RequestBody body: ForgotPasswordRequest
	) {
		emailRateLimiter.withRateLimit(
			email = body.email
		) {
			emailVerificationService.resendVerificationEmail(body.email)
		}
	}
	
	@GetMapping("/verify-email")
	fun verifyEmail(
		@RequestParam token: String
	) {
		emailVerificationService.verifyEmail(token)
	}
	
	@PostMapping("/reset-password")
	fun resetPassword(
		@Valid
		@RequestBody body: ResetPasswordRequest
	) {
		passwordResetService.resetPassword(
			token = body.token,
			newPassword = body.newPassword
		)
	}
	
	@PostMapping("/forgot-password")
	@IpRateLimit(requests = 10, duration = 1L, unit = TimeUnit.HOURS)
	fun forgotPassword(
		@Valid
		@RequestBody body: ForgotPasswordRequest
	) {
		passwordResetService.requestPasswordReset(
			email = body.email
		)
	}
	
	@PostMapping("/change-password")
	fun changePassword(
		@Valid
		@RequestBody body: ChangePasswordRequest
	) {
		// TODO: extract request user id and call service.
	}
}