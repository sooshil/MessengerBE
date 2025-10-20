package com.sukajee.chirpbe.api.config

import com.sukajee.chirpbe.domain.exception.RateLimitException
import com.sukajee.chirpbe.infra.rate_limiting.IpRateLimiter
import com.sukajee.chirpbe.infra.rate_limiting.IpResolver
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Duration
import kotlin.jvm.java

@Component
class IpRateLimitInterceptor(
	private val ipRateLimiter: IpRateLimiter,
	private val ipResolver: IpResolver,
	@param:Value("\${chirpbe.rate-limit.ip.apply-limit}")
	private val applyLimit: Boolean
): HandlerInterceptor {
	
	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
		if(handler is HandlerMethod && applyLimit) {
			val annotation = handler.getMethodAnnotation(IpRateLimit::class.java)
			if(annotation != null) {
				val clientIp = ipResolver.getClientIp(request)
				val path = request.requestURI
				
				return try {
					ipRateLimiter.withIpRateLimit(
						ipAddress = clientIp,
						path = path,
						resetsIn = Duration.of(
							annotation.duration,
							annotation.unit.toChronoUnit()
						),
						maxRequestsPerIp = annotation.requests,
						action = { true }
					)
					true
				}
				catch(exception: RateLimitException) {
					response.sendError(429) // Too many requests
					false
				}
			}
		}
		return true
	}
}