package org.kiss.minio

import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.http.Method
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/minio")
class MinioService(val minioClient: MinioClient) {

    @GetMapping("/presignedUrl")
    fun getPresignedUrl(
        @RequestParam method: Method,
        @RequestParam bucket: String,
        @RequestParam `object`: String
    ): String {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(method)
                .bucket(bucket)
                .`object`(`object`)
                .expiry(2, TimeUnit.HOURS)
                .build()
        )
    }
}