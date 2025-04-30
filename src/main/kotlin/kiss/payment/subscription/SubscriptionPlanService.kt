package kiss.payment.subscription

import kiss.payment.subscription.dto.SubscriptionPlanInput
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/subscription/plans")
class SubscriptionPlanService(val sql: KSqlClient) {

    @GetMapping
    fun list(): List<@FetchBy("LIST_ITEM") SubscriptionPlan> = sql.findAll(LIST_ITEM)

    @Transactional
    @PostMapping
    fun create(@RequestBody input: SubscriptionPlanInput) {
        sql.save(input, SaveMode.INSERT_ONLY)
    }

    companion object {
        val LIST_ITEM = newFetcher(SubscriptionPlan::class).by {
            allScalarFields()
        }
    }
}