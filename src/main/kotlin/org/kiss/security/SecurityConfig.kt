package org.kiss.security

import org.babyfish.jimmer.spring.cfg.JimmerProperties
import org.kiss.Constants
import org.kiss.rbac.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jimmerProperties: JimmerProperties,
        environment: Environment
    ): SecurityFilterChain {
        val openapiUiPath = jimmerProperties.client.openapi.uiPath
        val openapiPath = jimmerProperties.client.openapi.path
        val tsPath = jimmerProperties.client.ts.path ?: error("jimmer.client.ts.path not set")

        http {
            cors { }
            csrf { disable() }
            authorizeHttpRequests {
                authorize(HttpMethod.GET, openapiUiPath, permitAll)
                authorize(HttpMethod.GET, openapiPath, permitAll)
                authorize(HttpMethod.GET, tsPath, permitAll)
                authorize(Constants.ERROR_PATH, permitAll)
                authorize(HttpMethod.POST, AuthenticationService.URI, permitAll)
                authorize(anyRequest, authenticated)
            }
            exceptionHandling {
                authenticationEntryPoint = Http401UnauthorizedEntryPoint()
            }
        }

        return http.build()
    }

    @Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService {
        return JdbcUserDetailsService(userRepository)
    }

    @Bean
    fun authenticationManager(
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder
    ): AuthenticationManager {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder)

        return ProviderManager(authenticationProvider)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }
}