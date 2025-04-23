package kiss.feishu

import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feishu")
class FeishuService(
    val feishuApi: FeishuApi,
    val sql: KSqlClient,
) {

    @PostMapping("/authorize")
    fun authorize(
        @RequestParam code: String,
        @RequestParam redirectUri: String,
    ) {
        val accessToken = feishuApi.getUserAccessToken(code, redirectUri)
        val userInfo = feishuApi.getUserInfo(accessToken)

//        sql.save(FeishuUser {
//            name = userInfo.name
//            enName = userInfo.enName
//            avatarUrl = userInfo.avatarUrl
//            avatarThumb = userInfo.avatarThumb
//            avatarMiddle = userInfo.avatarMiddle
//            avatarBig = userInfo.avatarBig
//            openId = userInfo.openId
//            unionId = userInfo.unionId
//            email = userInfo.email
//            enterpriseEmail = userInfo.enterpriseEmail
//            feishuUserId = userInfo.userId
//            mobile = userInfo.mobile
//            tenantKey = userInfo.tenantKey
//            employeeNo = userInfo.employeeNo
//            user {
//                displayName = userInfo.name
//            }
//        })
    }
}