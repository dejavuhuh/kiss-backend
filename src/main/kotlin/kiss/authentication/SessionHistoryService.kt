package kiss.authentication

import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Transactional
@RestController
@RequestMapping("/session-histories")
class SessionHistoryService(val sql: KSqlClient) {

    @GetMapping
    fun list(
        @RequestParam pageIndex: Int,
        @RequestParam pageSize: Int,
    ) {

    }
}
