package com.sukajee.chirpbe.service.auth

import com.sukajee.chirpbe.domain.exception.InvalidCredentialsException
import com.sukajee.chirpbe.domain.exception.PasswordEncodeException
import com.sukajee.chirpbe.domain.exception.UserAlreadyExistsException
import com.sukajee.chirpbe.domain.exception.UserNotFoundException
import com.sukajee.chirpbe.domain.model.AuthenticatedUser
import com.sukajee.chirpbe.domain.model.User
import com.sukajee.chirpbe.domain.model.UserId
import com.sukajee.chirpbe.infra.database.entities.RefreshTokenEntity
import com.sukajee.chirpbe.infra.database.entities.UserEntity
import com.sukajee.chirpbe.infra.database.mappers.toUser
import com.sukajee.chirpbe.infra.database.repositories.RefreshTokenRepository
import com.sukajee.chirpbe.infra.database.repositories.UserRepository
import com.sukajee.chirpbe.infra.security.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
	private val userRepository: UserRepository,
	private val refreshTokenRepository: RefreshTokenRepository,
	private val passwordEncoder: PasswordEncoder,
	private val jwtService: JwtService
) {
	fun register(username: String, email: String, password: String): User {
		val user = userRepository.findByEmailOrUsername(
			email = email.trim(),
			username = username.trim()
		)
		if (user != null) throw UserAlreadyExistsException()
		val hashedPassword = passwordEncoder.encode(password) ?: throw PasswordEncodeException()
		val savedUser = userRepository.save(
			UserEntity(
				username = username.trim(),
				email = email.trim(),
				hashedPassword = hashedPassword,
			)
		)
		return savedUser.toUser()
	}
	
	fun login(
		email: String,
		password: String
	): AuthenticatedUser {
		val user = userRepository.findByEmail(email.trim())
			?: throw InvalidCredentialsException()
		
		if (!passwordEncoder.matches(password, user.hashedPassword)) {
			throw InvalidCredentialsException()
		}
		
		//TODO: check for verified email
		
		return user.id?.let {
			val accessToken = jwtService.generateAccessToken(userId = it)
			val refreshToken = jwtService.generateRefreshToken(userId = it)
			
			storeRefreshToken(userId = it, token = refreshToken)
			
			AuthenticatedUser(
				user = user.toUser(),
				accessToken = accessToken,
				refreshToken = refreshToken
			)
		} ?: throw UserNotFoundException()
	}
	
	private fun storeRefreshToken(userId: UserId, token: String) {
		val hashed = hashToken(token)
		val expiryMs = jwtService.refreshTokenValidityMs
		val expiresAt = Instant.now().plusMillis(expiryMs)
		refreshTokenRepository.save(
			RefreshTokenEntity(
				userId = userId,
				hashedToken = hashed,
				expiresAt = expiresAt
			)
		)
	}
	
	private fun hashToken(token: String): String {
		val digest = MessageDigest.getInstance("SHA_256")
		val hashBytes = digest.digest(token.encodeToByteArray())
		return Base64.getEncoder().encodeToString(hashBytes)
	}
}