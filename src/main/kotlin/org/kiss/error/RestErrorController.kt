package org.kiss.error

import jakarta.servlet.http.HttpServletRequest
import org.kiss.Constants
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RestErrorController(
    errorAttributes: ErrorAttributes,
    errorViewResolvers: ObjectProvider<ErrorViewResolver>
) : AbstractErrorController(errorAttributes, errorViewResolvers.orderedStream().toList()) {

    @RequestMapping(Constants.ERROR_PATH)
    fun error(request: HttpServletRequest): ProblemDetail {
        val status = getStatus(request)
        if (status == HttpStatus.NO_CONTENT) {
            return ProblemDetail.forStatus(HttpStatus.NOT_FOUND)
        }

        val body = getErrorAttributes(request, ErrorAttributeOptions.defaults())
        val detail = body["message"] as String?
        return ProblemDetail.forStatusAndDetail(status, detail)
    }
}