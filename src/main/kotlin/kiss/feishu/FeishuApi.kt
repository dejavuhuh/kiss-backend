package kiss.feishu

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.benmanes.caffeine.cache.Caffeine
import kiss.json.JsonSerializer
import kiss.okhttp.json
import kiss.system.config.ConfigCenter
import kiss.system.config.readYamlAsObject
import okhttp3.Request
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class FeishuApi(val configCenter: ConfigCenter) {

    val client = FeishuHttpClient()
    val baseUrl = "https://open.feishu.cn/open-apis"

    private val tenantAccessTokenCache = Caffeine
        .newBuilder()
        // nearly 2 hours
        .expireAfterWrite(Duration.ofSeconds(7100))
        .maximumSize(1)
        .build<String, String>()

    /**
     * 发送卡片消息到群聊
     */
    fun sendCardMessageToChat(
        chatId: String,
        templateId: String,
        templateVariables: Map<String, Any>,
        uuid: String,
    ) {
        val tenantAccessToken = getTenantAccessToken()

        data class RequestBody(
            @get:JsonGetter("receive_id")
            val receiveId: String,
            @get:JsonGetter("msg_type")
            val msgType: String,
            val content: String,
            val uuid: String,
        )

        val content = mapOf(
            "type" to "template",
            "data" to mapOf(
                "template_id" to templateId,
                "template_variable" to templateVariables
            )
        )

        val request = Request.Builder()
            .url("${baseUrl}/im/v1/messages?receive_id_type=chat_id")
            .header("Authorization", "Bearer $tenantAccessToken")
            .json(
                RequestBody(
                    receiveId = chatId,
                    msgType = "interactive",
                    content = JsonSerializer.serialize(content),
                    uuid = uuid,
                )
            )
            .build()

        client.execute<Unit>(request)
    }

    fun createChat(): String {
        val tenantAccessToken = getTenantAccessToken()

        data class RequestBody(
            val name: String,
            @get:JsonGetter("user_id_list")
            val userIdList: List<String>,
            @get:JsonGetter("user_id_type")
            val userIdType: String,
        )

        val request = Request.Builder()
            .url("${baseUrl}/im/v1/chats")
            .header("Authorization", "Bearer $tenantAccessToken")
            .json(
                RequestBody(
                    name = "测试群聊 123",
                    userIdList = listOf("123"),
                    userIdType = "open_id",
                )
            )
            .build()

        data class ResponseData(
            @JsonProperty("chat_id")
            val chatId: String,
        )

        val responseData = client.execute<ResponseData>(request)
        return responseData.chatId
    }

    private fun getTenantAccessToken() = tenantAccessTokenCache.get("tenantAccessToken") {
        loadTenantAccessToken()
    }

    private fun loadTenantAccessToken(): String {
        val config = getConfig()

        data class RequestBody(
            @get:JsonGetter("app_id") val appId: String,
            @get:JsonGetter("app_secret") val appSecret: String
        )

        data class ResponseBody(
            val code: Long,
            val msg: String,
            @JsonProperty("tenant_access_token")
            val tenantAccessToken: String,
            val expire: Long,
        )

        val request = Request.Builder()
            .url("${baseUrl}/auth/v3/tenant_access_token/internal")
            .json(RequestBody(config.appId, config.appSecret))
            .build()

        val responseBody = client.executeRaw<ResponseBody>(request)
        if (responseBody.code != OK) {
            throw FeishuApiException(responseBody.code, responseBody.msg)
        }

        return responseBody.tenantAccessToken
    }

    private fun getConfig() = configCenter.readYamlAsObject<FeishuConfig>("feishu")
}

data class FeishuConfig(
    val appId: String,
    val appSecret: String,
)
