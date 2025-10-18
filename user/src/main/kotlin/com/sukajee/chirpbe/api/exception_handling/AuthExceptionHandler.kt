package com.sukajee.chirpbe.api.exception_handling

import com.sukajee.chirpbe.domain.exception.EmailNotVerifiedException
import com.sukajee.chirpbe.domain.exception.IncorrectOldPasswordException
import com.sukajee.chirpbe.domain.exception.InvalidCredentialsException
import com.sukajee.chirpbe.domain.exception.InvalidTokenException
import com.sukajee.chirpbe.domain.exception.PasswordEncodeException
import com.sukajee.chirpbe.domain.exception.RateLimitException
import com.sukajee.chirpbe.domain.exception.SamePasswordException
import com.sukajee.chirpbe.domain.exception.UserAlreadyExistsException
import com.sukajee.chirpbe.domain.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {
	
	@ExceptionHandler(UserAlreadyExistsException::class)
	@ResponseStatus(HttpStatus.CONFLICT)
	fun onUserAlreadyExists(e: UserAlreadyExistsException) = mapOf(
		"code" to "USER_EXISTS",
		"message" to e.message
	)
	
	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun onValidationException(
		e: MethodArgumentNotValidException
	): ResponseEntity<Map<String, Any>> {
		val errors = e.bindingResult.allErrors.map {
			it.defaultMessage ?: "Invalid value"
		}
		
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(
				mapOf(
					"code" to "VALIDATION_ERROR",
					"errors" to errors
				)
			)
	}
	
	@ExceptionHandler(PasswordEncodeException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	fun onPasswordEncodeException(e: PasswordEncodeException) = mapOf(
		"code" to "INTERNAL_SERVER_ERROR",
		"message" to e.message
	)
	
	@ExceptionHandler(InvalidTokenException::class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	fun onInvalidToken(e: InvalidTokenException) = mapOf(
		"code" to "INVALID_TOKEN",
		"message" to e.message
	)
	
	@ExceptionHandler(UserNotFoundException::class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	fun onUserNotFound(e: UserNotFoundException) = mapOf(
		"code" to "USER_NOT_FOUND",
		"message" to e.message
	)
	
	@ExceptionHandler(InvalidCredentialsException::class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	fun onInvalidCredentials(e: InvalidCredentialsException) = mapOf(
		"code" to "INVALID_CREDENTIALS",
		"message" to e.message
	)
	
	@ExceptionHandler(EmailNotVerifiedException::class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	fun onEmailNotVerified(e: EmailNotVerifiedException) = mapOf(
		"code" to "EMAIL_NOT_VERIFIED",
		"message" to e.message
	)
	
	@ExceptionHandler(SamePasswordException::class)
	@ResponseStatus(HttpStatus.CONFLICT)
	fun onSamePassword(e: SamePasswordException) = mapOf(
		"code" to "SAME_NEW_PASSWORD",
		"message" to e.message
	)
	
	@ExceptionHandler(IncorrectOldPasswordException::class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	fun onIncorrectOldPassword(e: IncorrectOldPasswordException) = mapOf(
		"code" to "INCORRECT_OLD_PASSWORD",
		"message" to e.message
	)
	
	@ExceptionHandler(RateLimitException::class)
	@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
	fun onRateLimitException(e: RateLimitException) = mapOf(
		"code" to "RATE_LIMIT_EXCEEDED",
		"message" to e.message
	)
}