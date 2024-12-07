package org.kiss.rbac.repository

import org.babyfish.jimmer.View
import org.babyfish.jimmer.spring.repo.support.AbstractKotlinRepository
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.kiss.rbac.entity.User
import org.kiss.rbac.entity.username
import org.springframework.stereotype.Repository
import kotlin.reflect.KClass

@Repository
class UserRepository(sql: KSqlClient) : AbstractKotlinRepository<User, Long>(sql) {

    fun <S : View<User>> findByUsername(username: String, staticType: KClass<S>): S? {
        return createQuery {
            where(table.username eq username)
            select(table.fetch(staticType))
        }.fetchOneOrNull()
    }
}