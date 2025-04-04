package kiss.issue

import kiss.s3.S3Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/issues")
class IssueService(val s3Service: S3Service) {

    @PostMapping
    fun report() {

    }
}