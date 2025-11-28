package com.sukajee.chirpbe.infra.database.repositories

import com.sukajee.chirpbe.domain.type.UserId
import com.sukajee.chirpbe.infra.database.entities.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository: JpaRepository<RefreshTokenEntity, Long> {

	fun findByUserIdAndHashedToken(
		userId: UserId,
		hashedToken: String
	): RefreshTokenEntity?
	
	fun deleteByUserIdAndHashedToken(
		userId: UserId,
		hashedToken: String
	)
	
	fun deleteByUserId(userId: UserId): RefreshTokenEntity?
}