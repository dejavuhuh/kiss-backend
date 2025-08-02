package kiss.infrastructure.s3

import io.minio.*
import io.minio.http.Method
import jakarta.annotation.PostConstruct
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.InputStream
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * S3服务
 */
@RestController
@RequestMapping("/s3")
class S3Service(val minioClient: MinioClient) {

    @PostConstruct
    fun initBuckets() {
        val buckets = listOf("user-upload", "static-resource")
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
        @RequestParam(required = false) fileName: String? = null,
        @RequestParam(required = false) contentType: MediaType? = null,
    ): String {
        val extraQueryParams = mutableMapOf<String, String>()
        if (fileName != null) {
            val encodedFileName = URLEncoder.encode(fileName, Charsets.UTF_8)
            extraQueryParams["response-content-disposition"] = "attachment; filename*=UTF-8''$encodedFileName"
        }
        if (contentType != null) {
            extraQueryParams["response-content-type"] = contentType.toString()
        }

        val args = GetPresignedObjectUrlArgs.builder()
            .method(method)
            .bucket(bucket)
            .`object`(objectName)
            .expiry(1, TimeUnit.MINUTES)
            .extraQueryParams(extraQueryParams)
            .build()
        return minioClient.getPresignedObjectUrl(args)
    }

    fun putObject(objectName: String, inputStream: InputStream) {
        val args = PutObjectArgs.builder()
            .bucket("static-resource")
            .`object`(objectName)
            .stream(inputStream, -1, 5242880)
            .build()
        minioClient.putObject(args)
    }
}
