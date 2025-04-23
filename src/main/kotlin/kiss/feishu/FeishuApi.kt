package kiss.feishu

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.benmanes.caffeine.cache.Caffeine
import com.lark.oapi.Client
import com.lark.oapi.core.request.RequestOptions
import com.lark.oapi.service.authen.v1.model.GetUserInfoRespBody
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

    fun getUserInfo(accessToken: String): GetUserInfoRespBody {
        val (appId, appSecret) = getConfig()
        val sdkClient = Client.newBuilder(appId, appSecret).build()
        val response = sdkClient.authen().v1().userInfo().get(
            RequestOptions.newBuilder()
                .userAccessToken(accessToken)
                .build()
        )

        if (!response.success()) {
            throw FeishuApiException(response)
        }

        return response.data
    }

    /**
     * 获取 user_access_token
     */
    fun getUserAccessToken(
        code: String,
        redirectUri: String,
    ): String {
        val config = getConfig()

        data class RequestBody(
            val grant_type: String = "authorization_code",
            val client_id: String,
            val client_secret: String,
            val code: String,
            val redirect_uri: String,
        )

        val request = Request.Builder()
            .url("${baseUrl}/authen/v2/oauth/token")
            .json(
                RequestBody(
                    client_id = config.appId,
                    client_secret = config.appSecret,
                    code = code,
                    redirect_uri = redirectUri,
                )
            )
            .build()

        data class ResponseBody(
            val code: Long,
            val access_token: String,
            val expires_in: Long,
            val error: String?,
            val error_description: String?,
        )

        val response = client.executeRaw<ResponseBody>(request)
        if (response.code != OK) {
            throw FeishuAccessTokenException(response.code, response.error, response.error_description)
        }

        return response.access_token
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
            throw FeishuApiHttpException(responseBody.code, responseBody.msg)
        }

        return responseBody.tenantAccessToken
    }

    private fun getConfig() = configCenter.readYamlAsObject<FeishuConfig>("feishu")
}

data class FeishuConfig(
    val appId: String,
    val appSecret: String,
)
