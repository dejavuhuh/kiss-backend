package org.kiss.security

import org.kiss.rbac.entity.dto.UserPrincipal
import org.kiss.rbac.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class JdbcUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val userPrincipal = userRepository.findByUsername(username, UserPrincipal::class)
            ?: throw UsernameNotFoundException(username)
        return JdbcUserDetails(userPrincipal)
    }
}