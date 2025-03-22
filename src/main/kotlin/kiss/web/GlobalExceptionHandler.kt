package kiss.web

import io.github.oshai.kotlinlogging.KotlinLogging
import kiss.authentication.AuthenticationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler
    fun handleBusinessException(ex: BusinessException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message)
    }

    @ExceptionHandler
    fun handleAuthenticationException(ex: AuthenticationException): ProblemDetail {
        log.error(ex) { "认证异常" }
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.message)
    }

    @ExceptionHandler
    fun handleUnexpectedException(ex: Exception): ProblemDetail {
        log.error(ex) { "服务器内部错误" }
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误")
    }
}