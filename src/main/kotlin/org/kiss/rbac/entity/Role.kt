package org.kiss.rbac.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Key
import org.babyfish.jimmer.sql.ManyToMany
import org.kiss.IdAware

@Entity
interface Role : IdAware {

    @Key
    val name: String

    val description: String?

    @ManyToMany(mappedBy = "roles")
    val users: List<User>

    @ManyToMany
    val menus: List<Menu>
}