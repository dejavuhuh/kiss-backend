package kiss.alert

import kiss.feishu.FeishuApi
import kiss.business.system.config.ConfigCenter
import kiss.business.system.config.readYamlAsObject
import org.springframework.stereotype.Component

@Component
class AlertManager(
    val configCenter: ConfigCenter,
    val feishuApi: FeishuApi,
) {

    fun sendAlertMessage(
        title: String,
        context: Map<String, Any?>,
        digest: String,
    ) {
        val config = configCenter.readYamlAsObject<AlertConfig>("alert")
        val feishuConfig = config.feishu
        feishuApi.sendCardMessageToChat(
            chatId = feishuConfig.chatId,
            templateId = feishuConfig.card.templateId,
            templateVariables = mapOf(
                "title" to title,
                "fields" to context.map { (k, v) -> Field(k, v) }
            ),
            uuid = digest,
        )
    }
}

data class Field(val label: String, val value: Any?)

data class AlertConfig(val feishu: FeishuConfig)

data class FeishuConfig(
    val chatId: String,
    val card: Card,
)

data class Card(val templateId: String)