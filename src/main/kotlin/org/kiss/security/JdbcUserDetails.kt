package org.kiss.security

import org.kiss.rbac.entity.dto.UserPrincipal
import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JdbcUserDetails(userPrincipal: UserPrincipal) : UserDetails, CredentialsContainer {
    private var password: String? = userPrincipal.password
    private val username: String = userPrincipal.username
    val id: Long = userPrincipal.id
    private val authorities = userPrincipal.roles.map { SimpleGrantedAuthority(it.name) }

    override fun getAuthorities() = authorities
    override fun getPassword() = password
    override fun getUsername() = username
    override fun eraseCredentials() {
        password = null
    }
}