package kiss.feishu

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.benmanes.caffeine.cache.Caffeine
import kiss.okhttp.json
import kiss.system.config.ConfigCenter
import kiss.system.config.readYamlAsObject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class FeishuApi(val configCenter: ConfigCenter) {
    val client = OkHttpClient()
    val baseUrl = "https://open.feishu.cn/open-apis"
    private val tenantAccessTokenCache = Caffeine
        .newBuilder()
        // nearly 2 hours
        .expireAfterWrite(Duration.ofSeconds(7100))
        .maximumSize(1)
        .build<String, String>()

    fun createChat(): String {
        val tenantAccessToken = getTenantAccessToken()

        data class Body(
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
                Body(
                    name = "测试群聊 123",
                    userIdList = listOf("123"),
                    userIdType = "open_id",
                )
            )
            .build()

        data class Data(
            @JsonProperty("chat_id")
            val chatId: String,
        )

        val response = client.newCall(request).json<Response<Data>>()
        if (response.code != OK) {
            throw FeishuApiException(response.code, response.msg)
        }

        return response.data.chatId
    }

    private fun getTenantAccessToken() = tenantAccessTokenCache.get("tenantAccessToken") {
        loadTenantAccessToken()
    }

    private fun loadTenantAccessToken(): String {
        val config = getConfig()

        data class Body(
            @get:JsonGetter("app_id") val appId: String,
            @get:JsonGetter("app_secret") val appSecret: String
        )

        data class Response(
            val code: Long,
            val msg: String,
            @JsonProperty("tenant_access_token")
            val tenantAccessToken: String,
            val expire: Long,
        )

        val request = Request.Builder()
            .url("${baseUrl}/auth/v3/tenant_access_token/internal")
            .json(Body(config.appId, config.appSecret))
            .build()

        val response = client.newCall(request).json<Response>()
        if (response.code != OK) {
            throw FeishuApiException(response.code, response.msg)
        }

        return response.tenantAccessToken
    }

    private fun getConfig() = configCenter.readYamlAsObject<FeishuConfig>("feishu")
}

data class FeishuConfig(
    val appId: String,
    val appSecret: String,
)

const val OK = 0L

class FeishuApiException(code: Long, msg: String) : Exception("[${code}] $msg")

data class Response<T>(
    val code: Long,
    val msg: String,
    val data: T,
)