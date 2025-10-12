package com.sukajee.chirpbe.infra.database.repositories

import com.sukajee.chirpbe.infra.database.entities.EmailVerificationTokenEntity
import com.sukajee.chirpbe.infra.database.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface EmailVerificationTokenRepository: JpaRepository<EmailVerificationTokenEntity, Long> {
	fun findByToken(token: String): EmailVerificationTokenEntity?
	fun deleteByExpiresAtLessThan(now: Instant)
	
	// This function can be implemented with custom query as well.
	// Check PasswordResetTokenRepository for example.
	// And check Chirp Backend Course Chapter 4 Password Reset Token Database Setup video for more detail.
	fun findByUserAndUsedAtIsNull(user: UserEntity): List<EmailVerificationTokenEntity>
}