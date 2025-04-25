package kiss.feishu

import com.lark.oapi.service.authen.v1.model.GetUserInfoRespBody
import kiss.authentication.SessionRepository
import kiss.system.user.FeishuUser
import kiss.system.user.id
import kiss.system.user.userId
import org.babyfish.jimmer.sql.ast.mutation.AssociatedSaveMode
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feishu")
class FeishuService(
    val feishuApi: FeishuApi,
    val sql: KSqlClient,
    val sessionRepository: SessionRepository,
) {

    @PostMapping("/authorize")
    fun authorize(
        @RequestParam code: String,
        @RequestParam redirectUri: String,
    ): String {
        val accessToken = feishuApi.getUserAccessToken(code, redirectUri)
        val userInfo = feishuApi.getUserInfo(accessToken)

        return sql.transaction {
            val userId = sql.createQuery(FeishuUser::class) {
                where(table.id eq userInfo.userId)
                select(table.userId)
            }.fetchOneOrNull() ?: createNewUser(userInfo)

            sessionRepository.create(userId)
        }
    }

    private fun createNewUser(userInfo: GetUserInfoRespBody): Int {
        val savedUser = sql.save(FeishuUser {
            id = userInfo.userId
            user {
                displayName = userInfo.name
            }
        }, SaveMode.INSERT_ONLY, AssociatedSaveMode.APPEND).modifiedEntity.user

        return savedUser.id
    }
}