package com.sukajee.chirpbe.infra.database.repositories

import com.sukajee.chirpbe.domain.model.UserId
import com.sukajee.chirpbe.infra.database.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<UserEntity, UserId> {

	fun findByEmail(email: String): UserEntity?
	fun findByEmailOrUsername(email: String, username: String): UserEntity?
}