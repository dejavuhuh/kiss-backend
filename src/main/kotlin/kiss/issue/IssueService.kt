package kiss.issue

import kiss.issue.dto.IssueInput
import kiss.jimmer.insertOnly
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Transactional
@RestController
@RequestMapping("/issues")
class IssueService(val sql: KSqlClient) {

    @PostMapping
    fun report(@RequestBody input: IssueInput) {
        sql.insertOnly(input)
    }
}
