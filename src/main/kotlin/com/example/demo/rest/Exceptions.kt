package com.example.demo.rest

import org.springframework.http.HttpStatus
import java.lang.RuntimeException

enum class RestExceptionCode(val value: Long) {
    USER_NOT_FOUND(0x1),
    ARTICLE_NOT_FOUND(0x2)
}

open class RuntimeRESTException(
        message: String,
        var code: RestExceptionCode,
        var httpStatus: HttpStatus
) : RuntimeException(message)

class UserNotFoundException(login: String)
    : RuntimeRESTException(
        "Could not find user: '$login'",
        RestExceptionCode.USER_NOT_FOUND,
        HttpStatus.NOT_FOUND
    )

class ArticleNotFoundException(slug: String)
    : RuntimeRESTException(
        "Could not find article with slug: '$slug'",
        RestExceptionCode.ARTICLE_NOT_FOUND,
        HttpStatus.NOT_FOUND
    )