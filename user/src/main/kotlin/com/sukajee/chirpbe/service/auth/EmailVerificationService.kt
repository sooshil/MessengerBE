package com.sukajee.chirpbe.service.auth

import com.sukajee.chirpbe.domain.exception.InvalidTokenException
import com.sukajee.chirpbe.domain.exception.UserNotFoundException
import com.sukajee.chirpbe.domain.model.EmailVerificationToken
import com.sukajee.chirpbe.infra.database.entities.EmailVerificationTokenEntity
import com.sukajee.chirpbe.infra.database.mappers.toEmailVerificationToken
import com.sukajee.chirpbe.infra.database.mappers.toUser
import com.sukajee.chirpbe.infra.database.repositories.EmailVerificationTokenRepository
import com.sukajee.chirpbe.infra.database.repositories.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit


@Service
class EmailVerificationService(
	private val emailVerificationTokenRepository: EmailVerificationTokenRepository,
	private val userRepository: UserRepository,
	
	@param:Value($$"${chirp.email.verification.expiry-hours}")
	private val expiryHours: Long
) {
	@Transactional
	fun createEmailVerificationToken(
		email: String
	): EmailVerificationToken {
		val userEntity = userRepository.findByEmail(email)
			?: throw UserNotFoundException()
		
		val existingTokens = emailVerificationTokenRepository.findByUserAndUsedAtIsNull(
			user = userEntity
		)
		
		val now = Instant.now()
		val usedTokens = existingTokens.map {
			it.apply {
				this.usedAt = now
			}
		}
		emailVerificationTokenRepository.saveAll(usedTokens)
		
		val token = EmailVerificationTokenEntity(
			user = userEntity,
			expiresAt = now.plus(expiryHours, ChronoUnit.HOURS)
		)
		return emailVerificationTokenRepository.save(token).toEmailVerificationToken()
	}
	
	@Transactional
	fun verifyEmail(token: String) {
		val verificationToken = emailVerificationTokenRepository.findByToken(token)
			?: throw InvalidTokenException("Email verification token is invalid.")
		
		if(verificationToken.isUsed) {
			throw InvalidTokenException("Email verification token is already used.")
		}
		
		if(verificationToken.isExpired) {
			throw InvalidTokenException("Email verification token has already expired.")
		}
		
		emailVerificationTokenRepository.save(
			verificationToken.apply {
				this.usedAt = Instant.now()
			}
		)
		userRepository.save(
			verificationToken.user.apply {
				this.hasEmailVerified = true
			}
		).toUser()
	}
	
	@Scheduled(cron = "0 0 3 * * *")
	fun cleanupExpiredTokens() {
		emailVerificationTokenRepository.deleteByExpiresAtLessThan(
			now = Instant.now()
		)
	}
}