package com.sukajee.chirpbe.service

import com.sukajee.chirpbe.domain.exception.IncorrectOldPasswordException
import com.sukajee.chirpbe.domain.exception.InvalidTokenException
import com.sukajee.chirpbe.domain.exception.PasswordEncodeException
import com.sukajee.chirpbe.domain.exception.SamePasswordException
import com.sukajee.chirpbe.domain.exception.UserNotFoundException
import com.sukajee.chirpbe.domain.model.UserId
import com.sukajee.chirpbe.infra.database.entities.PasswordResetTokenEntity
import com.sukajee.chirpbe.infra.database.repositories.PasswordResetTokenRepository
import com.sukajee.chirpbe.infra.database.repositories.RefreshTokenRepository
import com.sukajee.chirpbe.infra.database.repositories.UserRepository
import com.sukajee.chirpbe.infra.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit


@Service
class PasswordResetService(
	private val passwordResetTokenRepository: PasswordResetTokenRepository,
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder,
	private val refreshTokenRepository: RefreshTokenRepository,
	
	@param:Value($$"${chirpbe.password-reset.expiry-minutes}")
	private val expiryMinutes: Long
) {
	@Transactional
	fun requestPasswordReset(email: String) {
		val user = userRepository.findByEmail(email) ?: return
		passwordResetTokenRepository.invalidateActiveTokensForUser(user)
		
		val token = PasswordResetTokenEntity(
			user = user,
			expiresAt = Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES)
		)
		passwordResetTokenRepository.save(token)
		
		//TODO: send notification about password reset
		
	}
	
	@Transactional
	fun resetPassword(token: String, newPassword: String) {
		val resetToken = passwordResetTokenRepository.findByToken(token)
			?: throw InvalidTokenException("Password reset token is invalid.")
		
		if (resetToken.isUsed) {
			throw InvalidTokenException("Password reset token has already been used.")
		}
		
		if (resetToken.isExpired) {
			throw InvalidTokenException("Password reset token has already been expired.")
		}
		
		val user = resetToken.user
		if (passwordEncoder.matches(newPassword, user.hashedPassword)) {
			throw SamePasswordException()
		}
		val hashedNewPassword = passwordEncoder.encode(newPassword) ?: throw PasswordEncodeException()
		userRepository.save(
			user.apply {
				this.hashedPassword = hashedNewPassword
			}
		)
		passwordResetTokenRepository.save(
			resetToken.apply {
				this.usedAt = Instant.now()
			}
		)
		user.id?.let { refreshTokenRepository.deleteByUserId(userId = it) }
	}
	
	@Transactional
	fun changePassword(
		oldPassword: String,
		newPassword: String,
		userId: UserId
	) {
		val user = userRepository.findByIdOrNull(userId)
			?: throw UserNotFoundException()
		
		if (!passwordEncoder.matches(oldPassword, user.hashedPassword)) {
			throw IncorrectOldPasswordException()
		}
		if (newPassword == oldPassword) {
			throw SamePasswordException()
		}
		user.id?.let { refreshTokenRepository.deleteByUserId(userId = it) }
		
		val newHashedPassword = passwordEncoder.encode(newPassword) ?: throw PasswordEncodeException()
		
		userRepository.save(
			user.apply {
				this.hashedPassword = newHashedPassword
			}
		
		)
	}
	
	@Scheduled(cron = "0 0 3 * * *") //at 3am every day
	fun cleanupExpiredTokens() {
		passwordResetTokenRepository.deleteByExpiresAtLessThan(
			now = Instant.now()
		)
	}
}