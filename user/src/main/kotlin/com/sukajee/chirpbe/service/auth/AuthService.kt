package com.sukajee.chirpbe.service.auth

import com.sukajee.chirpbe.domain.exception.PasswordEncodeException
import com.sukajee.chirpbe.domain.exception.UserAlreadyExistsException
import com.sukajee.chirpbe.domain.model.User
import com.sukajee.chirpbe.infra.database.entities.UserEntity
import com.sukajee.chirpbe.infra.database.mappers.toUser
import com.sukajee.chirpbe.infra.database.repositories.UserRepository
import com.sukajee.chirpbe.infra.security.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder
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
}