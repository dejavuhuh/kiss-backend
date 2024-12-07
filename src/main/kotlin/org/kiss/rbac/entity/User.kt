package org.kiss.rbac.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToMany
import org.babyfish.jimmer.sql.Table
import org.kiss.IdAware


@Entity
@Table(name = "\"user\"")
interface User : IdAware {

    @Key
    val username: String

    val password: String

    @ManyToMany
    val roles: List<Role>
}