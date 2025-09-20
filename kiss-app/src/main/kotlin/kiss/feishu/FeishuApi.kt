package kiss.feishu

import com.fasterxml.jackson.databind.ObjectMapper
import com.lark.oapi.Client
import com.lark.oapi.core.request.RequestOptions
import com.lark.oapi.core.response.BaseResponse
import com.lark.oapi.service.authen.v1.model.GetUserInfoRespBody
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum
import com.lark.oapi.service.im.v1.model.CreateMessageReq
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody
import kiss.business.system.config.ConfigCenter
import kiss.business.system.config.readYamlAsObject
import org.springframework.stereotype.Component

@Component
class FeishuApi(
    val configCenter: ConfigCenter,
    val objectMapper: ObjectMapper,
) {

    val config: FeishuConfig get() = configCenter.readYamlAsObject<FeishuConfig>("feishu")
    val client: Client
        get() {
            val (appId, appSecret) = config
            return Client.newBuilder(appId, appSecret).build()
        }

    /**
     * 发送卡片消息到群聊
     */
    fun sendCardMessageToChat(
        chatId: String,
        templateId: String,
        templateVariables: Map<String, Any>,
        uuid: String,
    ) {
        val content = mapOf(
            "type" to "template",
            "data" to mapOf(
                "template_id" to templateId,
                "template_variable" to templateVariables
            )
        )

        val request = CreateMessageReq.newBuilder()
            .receiveIdType(CreateMessageReceiveIdTypeEnum.CHAT_ID)
            .createMessageReqBody(
                CreateMessageReqBody.newBuilder()
                    .receiveId(chatId)
                    .msgType("interactive")
                    .content(objectMapper.writeValueAsString(content))
                    .uuid(uuid)
                    .build()
            )
            .build()

        val response = client.im().v1().message().create(request)
        if (!response.success()) {
            throw FeishuApiException(response)
        }
    }

    fun getUserInfo(accessToken: String): GetUserInfoRespBody {
        val response = client.authen().v1().userInfo().get(
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
        TODO("使用OAuth2客户端实现")
    }
}

data class FeishuConfig(
    val appId: String,
    val appSecret: String,
)

class FeishuApiException(response: BaseResponse<*>) : Exception(
    """飞书 API 调用失败
    |code: ${response.code}
    |msg: ${response.msg}
    |requestId: ${response.requestId}
    |rawResponse: ${String(response.rawResponse.body, Charsets.UTF_8)}
""".trimMargin()
)