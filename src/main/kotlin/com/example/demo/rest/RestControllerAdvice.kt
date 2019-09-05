package com.example.demo.rest

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.util.*

@RestControllerAdvice
class RestControllerAdvice {
    data class ErrorResponse(
            val status: Int,
            val timestamp: Long,
            val error: String,
            val code: Long
    )

    @ResponseBody
    @ExceptionHandler(RuntimeRESTException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun exceptionHandler(exception: RuntimeRESTException, req: WebRequest): ErrorResponse {
        return ErrorResponse(
                timestamp = Date().time,
                status = exception.httpStatus.value(),
                error = exception.message!!,
                code = exception.code.value
        )
    }
}