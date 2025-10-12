package com.sukajee.chirpbe.infra.database.repositories

import com.sukajee.chirpbe.infra.database.entities.PasswordResetTokenEntity
import com.sukajee.chirpbe.infra.database.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface PasswordResetTokenRepository: JpaRepository<PasswordResetTokenEntity, Long> {
	fun findByToken(token: String): PasswordResetTokenEntity?
	fun deleteByExpiresAtLessThan(now: Instant)
//	fun findByUserAndUsedAtIsNull(user: UserEntity): List<PasswordResetTokenEntity>
	
	//Custom Query (could have been done by above commented function way as well)
	@Modifying
	@Query(
		"""
			UPDATE PasswordResetTokenEntity p
			SET p.usedAt = CURRENT_TIMESTAMP
			WHERE p.user = :user AND p.usedAt IS NULL
		"""
	)
	fun invalidateActiveTokensForUser(user: UserEntity): List<PasswordResetTokenEntity>
}