package kiss

import kiss.junit.MockUserExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.MinIOContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockUserExtension::class)
@Testcontainers
@Import(TestcontainersConfiguration::class)
abstract class SpringBootIntegrationTest {
    
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
}
