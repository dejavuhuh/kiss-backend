package kiss.web

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kiss.alert.AlertManager
import kiss.trace.TraceIdHolder
import org.apache.commons.codec.digest.DigestUtils.md5Hex
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

private val log = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler(val alertManager: AlertManager) : ResponseEntityExceptionHandler() {

    @ExceptionHandler
    fun handleBusinessException(ex: BusinessException): ProblemDetail {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message)
    }

    @ExceptionHandler
    fun handleUnexpectedException(ex: Exception, request: HttpServletRequest): ProblemDetail {
        log.error(ex) { "服务器内部错误" }

        alertManager.sendAlertMessage(
            title = "HTTP接口异常",
            context = mapOf(
                "请求地址" to request.requestURI,
                "会话 ID" to MDC.get("sessionId"),
                "Trace ID" to TraceIdHolder.get(),
                "异常信息" to ex.message,
            ),
            digest = md5Hex(ex.stackTraceToString())
        )

        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误")
    }
}
