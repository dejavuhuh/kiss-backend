package kiss.system.role

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import kiss.TestcontainersConfiguration
import kiss.system.permission.Permission
import kiss.system.permission.PermissionType
import kiss.system.permission.addBy
import kiss.system.permission.id
import kiss.system.role.dto.RoleSpecification
import kiss.withTestUser
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.table.source
import org.babyfish.jimmer.sql.kt.ast.table.sourceId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@Import(TestcontainersConfiguration::class)
class RoleServiceTest @Autowired constructor(
    val sql: KSqlClient,
    val roleService: RoleService,
) {

    @Test
    fun `List roles by permissionId`() = withTestUser {
        // Prepare data
        val p1 = Permission {
            parentId = null
            type = PermissionType.DIRECTORY
            code = "p_code"
            name = "p_name"
        }
        val r1 = Role { name = "r_name_1"; permissions().addBy(p1) }
        val r2 = Role { name = "r_name_2" }
        val permissionId = sql
            .insertEntities(listOf(r1, r2))
            .items[0]
            .modifiedEntity
            .permissions[0]
            .id

        // Test
        roleService.list(RoleSpecification())
            .map { it.name }
            .shouldContainExactlyInAnyOrder(r1.name, r2.name)

        roleService.list(RoleSpecification(permissionId = permissionId))
            .map { it.name }
            .shouldContainExactlyInAnyOrder(r1.name)
    }
}
