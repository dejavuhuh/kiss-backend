package org.kiss.rbac.service

import org.babyfish.jimmer.Page
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.exception.SaveException
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.kiss.PageParam
import org.kiss.error.BusinessException
import org.kiss.jimmer.fetchPage
import org.kiss.jimmer.select
import org.kiss.rbac.entity.User
import org.kiss.rbac.entity.dto.UserInput
import org.kiss.rbac.entity.dto.UserSpecification
import org.kiss.rbac.entity.dto.UserView
import org.kiss.rbac.entity.id
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserService(
    val sqlClient: KSqlClient,
    val passwordEncoder: PasswordEncoder,
    @Value("\${user.default-password}") val defaultPassword: String
) {

    @GetMapping
    fun findByPage(pageParam: PageParam, specification: UserSpecification): Page<UserView> {
        return sqlClient.createQuery(User::class) {
            where(specification)
            orderBy(table.id.desc())
            select(UserView::class)
        }.fetchPage(pageParam)
    }

    @PostMapping
    fun create(@RequestBody input: UserInput) {
        try {
            val encodedPassword = passwordEncoder.encode(defaultPassword)
            sqlClient.insert(input.toEntity {
                password = encodedPassword
            })
        } catch (ex: SaveException.NotUnique) {
            throw BusinessException("Username '${input.username}' already exists")
        }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody input: UserInput) {
        try {
            sqlClient.update(
                input.toEntity { this.id = id },
                AssociatedSaveMode.REPLACE
            )
        } catch (ex: SaveException.NotUnique) {
            throw BusinessException("Username '${input.username}' already exists")
        }
    }

    @DeleteMapping("/{id}")
    fun deleteById(@PathVariable id: Long) {
        sqlClient.deleteById(User::class, id)
    }
}