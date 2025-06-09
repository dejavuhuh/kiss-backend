package kiss.system.permission

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kiss.SpringBootIntegrationTest
import kiss.junit.MockUser
import kiss.system.permission.dto.PermissionInput
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

@Sql(statements = ["INSERT INTO \"user\" (display_name) VALUES ('TEST_USER');"])
class PermissionServiceTest @Autowired constructor(
    val permissionService: PermissionService,
    val sql: KSqlClient,
) : SpringBootIntegrationTest() {

    @Test
    @MockUser(1)
    fun `Should create new permission when input is valid`() {
        val stubPermissionType = PermissionType.DIRECTORY
        val stubPermissionCode = "stubPermissionCode"
        val stubPermissionName = "stubPermissionName"
        val stubPermissionParentId = null

        val createdPermission = permissionService.create(
            PermissionInput(
                type = stubPermissionType,
                code = stubPermissionCode,
                name = stubPermissionName,
                parentId = stubPermissionParentId,
            )
        )

        createdPermission.type shouldBe stubPermissionType
        createdPermission.code shouldBe stubPermissionCode
        createdPermission.name shouldBe stubPermissionName

        // Check if the permission is created in the database
        val permissionInDB = sql.findById(Permission::class, createdPermission.id).shouldNotBeNull()
        permissionInDB.type shouldBe createdPermission.type
        permissionInDB.code shouldBe createdPermission.code
        permissionInDB.name shouldBe createdPermission.name
        permissionInDB.createdTime.shouldNotBeNull()
    }
}
