package kiss.feishu

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feishu")
class FeishuService(val feishuApi: FeishuApi) {

    @PostMapping("/authorize")
    fun authorize(
        @RequestParam code: String,
        @RequestParam redirectUri: String,
    ) {
        feishuApi.getUserAccessToken(code, redirectUri)
    }
}