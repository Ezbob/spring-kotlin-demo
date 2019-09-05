package com.example.demo.rest

import com.example.demo.domain.entity.User
import com.example.demo.domain.repository.UserRepository
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/user")
class UserController(private val repository: UserRepository) {

    @GetMapping("/")
    fun findAll() = repository.findAll()

    @GetMapping("/{login}")
    fun findOne(@PathVariable login: String): User {
        return repository.findByLogin(login) ?: throw UserNotFoundException(login)
    }
}
