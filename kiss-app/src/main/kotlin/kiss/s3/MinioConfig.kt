package kiss.s3

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig {

    @Bean
    fun minioClient(
        @Value("\${minio.endpoint}") endpoint: String,
        @Value("\${minio.access-key}") accessKey: String,
        @Value("\${minio.secret-key}") secretKey: String,
    ): MinioClient {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()
    }
}
