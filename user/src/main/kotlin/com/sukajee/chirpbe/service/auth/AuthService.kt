package com.sukajee.chirpbe.service.auth

import com.sukajee.chirpbe.domain.exception.InvalidCredentialsException
import com.sukajee.chirpbe.domain.exception.InvalidTokenException
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
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

/**
 * Service class for handling authentication-related operations such as user registration,
 * login, and token refreshing. It orchestrates the use of repositories, password encoders,
 * and JWT services to manage the authentication flow.
 *
 * @param userRepository Repository for user data access.
 * @param refreshTokenRepository Repository for storing and retrieving refresh tokens.
 * @param passwordEncoder Utility for encoding and verifying passwords.
 * @param jwtService Service for generating and validating JSON Web Tokens (JWTs).
 */
@Service
class AuthService(
	private val userRepository: UserRepository,
	private val refreshTokenRepository: RefreshTokenRepository,
	private val passwordEncoder: PasswordEncoder,
	private val jwtService: JwtService
) {
	/**
	 * Registers a new user in the system.
	 *
	 * This function handles the creation of a new user account. It checks for existing users
	 * with the same username or email, hashes the password for security, and saves the new
	 * user to the database.
	 *
	 * @param username The desired username for the new user.
	 * @param email The email address for the new user.
	 * @param password The plain-text password for the new user.
	 * @return The created [User] object.
	 * @throws UserAlreadyExistsException if a user with the same username or email already exists.
	 * @throws PasswordEncodeException if the password hashing fails.
	 */
	fun register(username: String, email: String, password: String): User {
		// Check if a user with the provided email or username already exists to prevent duplicates.
		val user = userRepository.findByEmailOrUsername(
			email = email.trim(),
			username = username.trim()
		)
		if (user != null) throw UserAlreadyExistsException()

		// Encode the plain-text password into a secure hash before storing it.
		val hashedPassword = passwordEncoder.encode(password) ?: throw PasswordEncodeException()

		// Create and save the new user entity to the database.
		val savedUser = userRepository.save(
			UserEntity(
				username = username.trim(),
				email = email.trim(),
				hashedPassword = hashedPassword,
			)
		)
		// Convert the saved entity to a domain model and return it.
		return savedUser.toUser()
	}

	/**
	 * Authenticates a user and provides access and refresh tokens upon successful login.
	 *
	 * This function verifies a user's credentials. If the email and password are correct,
	 * it generates a new set of JWTs (access and refresh tokens) and stores the refresh token.
	 *
	 * @param email The user's email address.
	 * @param password The user's plain-text password.
	 * @return An [AuthenticatedUser] object containing the user details and JWTs.
	 * @throws InvalidCredentialsException if the email is not found or the password does not match.
	 * @throws UserNotFoundException if the user ID is unexpectedly null after authentication.
	 */
	fun login(
		email: String,
		password: String
	): AuthenticatedUser {
		// Find the user by their email address. If not found, the credentials are invalid.
		val user = userRepository.findByEmail(email.trim())
			?: throw InvalidCredentialsException()

		// Use the password encoder to securely compare the provided password with the stored hash.
		if (!passwordEncoder.matches(password, user.hashedPassword)) {
			throw InvalidCredentialsException()
		}

		// TODO: check for verified email

		// If authentication is successful, generate tokens.
		return user.id?.let {
			val accessToken = jwtService.generateAccessToken(userId = it)
			val refreshToken = jwtService.generateRefreshToken(userId = it)

			// Store the new refresh token so it can be used for session renewal.
			storeRefreshToken(userId = it, token = refreshToken)

			AuthenticatedUser(
				user = user.toUser(),
				accessToken = accessToken,
				refreshToken = refreshToken
			)
		} ?: throw UserNotFoundException() // This should ideally not happen if the user was found.
	}

	/**
	 * Refreshes an authentication session using a valid refresh token.
	 *
	 * This function validates a given refresh token, revokes it, and issues a new pair of
	 * access and refresh tokens. This allows users to maintain their session without logging in again.
	 * The @Transactional annotation ensures that the deletion of the old token and creation of the new one
	 * happen as a single, atomic operation.
	 *
	 * @param refreshToken The refresh token provided by the client.
	 * @return A new [AuthenticatedUser] object with fresh tokens.
	 * @throws InvalidTokenException if the refresh token is invalid, expired, or not found.
	 * @throws UserNotFoundException if the user associated with the token no longer exists.
	 */
	@Transactional
	fun refresh(refreshToken: String): AuthenticatedUser {
		// Perform an initial validation on the JWT to check for expiration or malformation.
		if (!jwtService.validateRefreshToken(refreshToken)) {
			throw InvalidTokenException(
				message = "Invalid refresh token"
			)
		}
		// Extract the user ID from the token's claims.
		val userId = jwtService.getUserIdFromToken(refreshToken)
		val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

		// Hash the incoming token to look it up in the database.
		val hashed = hashToken(refreshToken)

		return user.id?.let { userId ->
			// Verify that the hashed refresh token exists in our database for the given user.
			refreshTokenRepository.findByUserIdAndHashedToken(
				userId = userId,
				hashedToken = hashed
			) ?: throw InvalidTokenException("Invalid refresh token")

			// The token is valid, so delete the old one to prevent reuse.
			refreshTokenRepository.deleteByUserIdAndHashedToken(
				userId = userId,
				hashedToken = hashed
			)

			// Issue a new pair of tokens.
			val newRefreshToken = jwtService.generateRefreshToken(userId)
			val newAccessToken = jwtService.generateAccessToken(userId)

			// Store the new refresh token.
			storeRefreshToken(userId, newRefreshToken)

			AuthenticatedUser(
				user = user.toUser(),
				accessToken = newAccessToken,
				refreshToken = newRefreshToken
			)

		} ?: throw UserNotFoundException()
	}
	
	@Transactional
	fun logout(refreshToken: String) {
		val userId = jwtService.getUserIdFromToken(refreshToken)
		val hashed = hashToken(refreshToken)
		refreshTokenRepository.deleteByUserIdAndHashedToken(
			userId = userId,
			hashedToken = hashed
		)
	}

	/**
	 * Hashes and stores a refresh token in the database.
	 *
	 * @param userId The ID of the user to whom the token belongs.
	 * @param token The plain-text refresh token.
	 */
	private fun storeRefreshToken(userId: UserId, token: String) {
		// Hash the token for secure storage.
		val hashed = hashToken(token)
		// Calculate the token's expiry date based on the JWT service configuration.
		val expiryMs = jwtService.refreshTokenValidityMs
		val expiresAt = Instant.now().plusMillis(expiryMs)
		// Save the token entity to the database.
		refreshTokenRepository.save(
			RefreshTokenEntity(
				userId = userId,
				hashedToken = hashed,
				expiresAt = expiresAt
			)
		)
	}

	/**
	 * Hashes a token string using SHA-256 and encodes it in Base64.
	 * This is a one-way hashing mechanism to securely store refresh tokens in the database,
	 * preventing session hijacking even if the database is compromised.
	 *
	 * @param token The plain-text token to hash.
	 * @return A Base64-encoded string representing the SHA-256 hash of the token.
	 */
	private fun hashToken(token: String): String {
		// Get a SHA-256 message digest instance.
		val digest = MessageDigest.getInstance("SHA-256")
		// Compute the hash of the token's byte representation.
		val hashBytes = digest.digest(token.encodeToByteArray())
		// Encode the raw hash bytes into a URL-safe Base64 string for easy storage.
		return Base64.getEncoder().encodeToString(hashBytes)
	}
}