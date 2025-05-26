package kiss.application

import kiss.application.dto.PermissionApplicationInput
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 权限申请管理
 */
@Transactional
@RestController
@RequestMapping("/permission-applications")
class PermissionApplicationService(val sql: KSqlClient) {

    /**
     * 发起权限申请
     */
    @PostMapping
    fun create(@RequestBody input: PermissionApplicationInput) {
        sql.save(input, SaveMode.INSERT_ONLY)
    }
}