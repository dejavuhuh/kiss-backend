package kiss.payment.recharge

import com.alipay.api.AlipayConfig
import com.alipay.api.DefaultAlipayClient
import com.alipay.api.diagnosis.DiagnosisUtils
import com.alipay.api.domain.AlipayTradePagePayModel
import com.alipay.api.request.AlipayTradePagePayRequest
import com.alipay.api.response.AlipayTradePagePayResponse
import kiss.payment.Order
import kiss.payment.OrderStatus
import kiss.system.config.ConfigCenter
import kiss.system.config.readYamlAsObject
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payment/recharge")
class RechargeService(
    val configCenter: ConfigCenter,
    val sql: KSqlClient,
) {

    @Transactional
    @GetMapping("/alipay/page")
    fun generateAlipayPage(@RequestParam price: String): String {
        val alipayConfig = configCenter.readYamlAsObject<AlipayConfig>("alipay")
        val config = AlipayConfig().apply {
            serverUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do"
            appId = alipayConfig.appId
            privateKey = alipayConfig.appPrivateKey
            format = "json"
            alipayPublicKey = alipayConfig.alipayPublicKey
            charset = "UTF-8"
            signType = "RSA2"
        }
        val client = DefaultAlipayClient(config)

        val savedOrder = sql.save(Order {
            status = OrderStatus.PENDING
        }, SaveMode.INSERT_ONLY).modifiedEntity

        val model = AlipayTradePagePayModel().apply {
            outTradeNo = savedOrder.id.toString()
            totalAmount = price
            subject = "测试充值服务"
            productCode = "FAST_INSTANT_TRADE_PAY"
            qrPayMode = "1"
        }

        val request = AlipayTradePagePayRequest().apply {
            bizModel = model
        }

        val response = client.pageExecute(request)
        if (response.isSuccess) {
            return response.body
        }

        throw AlipayException(response)
    }

    data class AlipayConfig(
        val appId: String,
        val appPrivateKey: String,
        val alipayPublicKey: String,
    )

    class AlipayException(response: AlipayTradePagePayResponse) :
        Exception(DiagnosisUtils.getDiagnosisUrl(response))
}