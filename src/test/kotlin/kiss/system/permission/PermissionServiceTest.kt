package kiss.system.permission

import io.kotest.matchers.shouldBe
import kiss.TestcontainersConfiguration
import kiss.junit.MockUser
import kiss.junit.MockUserExtension
import kiss.system.permission.dto.PermissionInput
import org.babyfish.jimmer.kt.isLoaded
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
@Sql(statements = ["INSERT INTO \"user\" (display_name) VALUES ('TEST_USER');"])
@ExtendWith(MockUserExtension::class)
@Import(TestcontainersConfiguration::class)
class PermissionServiceTest @Autowired constructor(val permissionService: PermissionService) {

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
        isLoaded(createdPermission, Permission::id) shouldBe true
    }
}
