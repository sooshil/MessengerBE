package com.sukajee.chirpbe.infra.database.entities

import com.sukajee.chirpbe.infra.security.TokenGenerator
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(
	name = "email_verification_tokens",
	schema = "user_service"
)
class EmailVerificationTokenEntity(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long = 0,
	
	@Column(nullable = false, unique = true)
	var token: String = TokenGenerator.generateSecureToken(),
	
	@Column(nullable = false)
	var expiresAt: Instant,
	
	@CreationTimestamp
	var createdAt: Instant = Instant.now(),
	
	@Column(nullable = false)
	var usedAt: Instant? = null,
	
	@JoinColumn(name = "user_id", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	var user: UserEntity
) {
	val isExpired: Boolean
		get() = expiresAt.isBefore(Instant.now())
	
	val isUsed: Boolean
		get() = usedAt != null
}