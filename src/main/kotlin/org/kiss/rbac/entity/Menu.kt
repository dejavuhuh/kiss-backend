package org.kiss.rbac.entity

import org.babyfish.jimmer.sql.*
import org.kiss.IdAware

@Entity
interface Menu : IdAware {

    @Key
    @ManyToOne
    @OnDissociate(DissociateAction.DELETE)
    val parent: Menu?

    @Key
    val name: String

    val title: String

    @Column(name = "\"order\"")
    val order: Int

    @OneToMany(mappedBy = "parent", orderedProps = [OrderedProp("order")])
    val children: List<Menu>

    @ManyToMany(mappedBy = "menus")
    val roles: List<Role>
}