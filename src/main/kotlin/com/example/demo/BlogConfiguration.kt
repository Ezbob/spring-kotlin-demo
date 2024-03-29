package com.example.demo

import com.example.demo.domain.entity.Article
import com.example.demo.domain.entity.User
import com.example.demo.domain.repository.ArticleRepository
import com.example.demo.domain.repository.UserRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BlogConfiguration {

    @Bean
    fun databaseInitializer(userRepository: UserRepository,
                            articleRepository: ArticleRepository) = ApplicationRunner {
        val smaldini = userRepository.save(User(
                login = "smaldini",
                firstname = "Stephane",
                lastname = "Maldini"
        ))
        articleRepository.save(Article(
                title = "Reactor Bismuth is out",
                headline = "Lorem ipsum",
                content = "dolor set ament",
                author = smaldini
        ))
        articleRepository.save(Article(
                title = "Reactor Aluminium has landed",
                headline = "Lorem ipsum",
                content = "dolor set ament",
                author = smaldini
        ))
    }
}