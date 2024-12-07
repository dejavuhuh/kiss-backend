package org.kiss.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(AuthenticationService.URI)
class AuthenticationService(val authenticationManager: AuthenticationManager) {

    val securityContextRepository = HttpSessionSecurityContextRepository()
    val logoutHandler = SecurityContextLogoutHandler()

    @GetMapping
    fun getCurrentUser(request: HttpServletRequest): JdbcUserDetails {
        val auth = request.userPrincipal as Authentication
        return auth.principal as JdbcUserDetails
    }

    @PostMapping
    fun login(
        @RequestBody loginRequest: LoginRequest,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val unauthenticatedToken = UsernamePasswordAuthenticationToken.unauthenticated(
            loginRequest.username, loginRequest.password
        )
        val authentication = authenticationManager.authenticate(unauthenticatedToken)
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication
        securityContextRepository.saveContext(context, request, response)
    }

    @DeleteMapping
    fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        SecurityContextHolder.getContext().authentication?.also {
            logoutHandler.logout(request, response, it)
        }
    }

    data class LoginRequest(val username: String, val password: String)

    companion object {
        const val URI = "/api/authentication"
    }
}