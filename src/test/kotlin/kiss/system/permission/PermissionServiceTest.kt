package kiss.system.permission

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kiss.TestcontainersConfiguration
import kiss.junit.MockUser
import kiss.junit.MockUserExtension
import kiss.system.permission.dto.PermissionInput
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.MinIOContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Transactional
@SpringBootTest
@Sql(statements = ["INSERT INTO \"user\" (display_name) VALUES ('TEST_USER');"])
@ExtendWith(MockUserExtension::class)
@Testcontainers
@Import(TestcontainersConfiguration::class)
class PermissionServiceTest @Autowired constructor(
    val permissionService: PermissionService,
    val sql: KSqlClient,
) {

    companion object {
        @Container
        @JvmStatic
        private val minioContainer = MinIOContainer(DockerImageName.parse("minio/minio:latest"))
            .withUserName("kiss")
            .withPassword("kisskiss")

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("minio.endpoint", minioContainer::getS3URL)
            registry.add("minio.access-key", minioContainer::getUserName)
            registry.add("minio.secret-key", minioContainer::getPassword)
        }
    }

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
