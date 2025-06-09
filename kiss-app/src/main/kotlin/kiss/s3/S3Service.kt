package kiss.s3

import io.minio.BucketExistsArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.http.Method
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

/**
 * S3服务
 */
@RestController
@RequestMapping("/s3")
class S3Service(
    @Value("\${minio.endpoint}") val endpoint: String,
    @Value("\${minio.access-key}") val accessKey: String,
    @Value("\${minio.secret-key}") val secretKey: String,
) {
    private val minioClient = MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build()

    @PostConstruct
    fun initBuckets() {
        val buckets = listOf("system-error-screenshot", "export-task")
        for (bucket in buckets) {
            val found = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build()
            )
            if (!found) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build()
                )
            }
        }
    }

    /**
     * 获取预签名URL
     */
    @GetMapping("/preSignedUrl")
    fun preSignedUrl(
        @RequestParam bucket: String,
        @RequestParam method: Method,
        @RequestParam objectName: String,
    ): String {
        val args = GetPresignedObjectUrlArgs.builder()
            .method(method)
            .bucket(bucket)
            .`object`(objectName)
            .expiry(1, TimeUnit.MINUTES)
            .build()
        return minioClient.getPresignedObjectUrl(args)
    }
}
