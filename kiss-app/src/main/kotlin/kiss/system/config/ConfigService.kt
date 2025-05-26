package kiss.system.config

import kiss.jimmer.insertOnly
import kiss.jimmer.updateOnly
import kiss.system.config.dto.ConfigInput
import kiss.system.config.dto.SaveYamlInput
import kiss.web.BusinessException
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.exception.SaveException
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.desc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * 配置管理
 */
@Transactional
@RestController
@RequestMapping("/config")
class ConfigService(val sql: KSqlClient) {

    /**
     * 查询配置列表
     */
    @GetMapping
    fun list(): List<@FetchBy("LIST_ITEM") Config> = sql.executeQuery(Config::class) {
        select(table.fetch(LIST_ITEM))
    }

    /**
     * 创建配置
     */
    @PostMapping
    fun create(@RequestBody input: ConfigInput) {
        sql.insertOnly(input)
    }

    /**
     * 保存YAML
     */
    @PutMapping("/{id}/save-yaml")
    fun saveYaml(@PathVariable id: Int, @RequestBody input: SaveYamlInput) {
        try {
            sql.updateOnly(input.toEntity {
                this.id = id
            })
        } catch (_: SaveException.OptimisticLockError) {
            throw BusinessException("当前配置文件已被其他用户修改，请刷新页面后重试")
        }

        sql.insertOnly(ConfigHistory {
            configId = id
            yaml = input.yaml
            reason = input.reason
        })
    }

    /**
     * 查询配置详情
     */
    @GetMapping("/{id}")
    fun get(@PathVariable id: Int): @FetchBy("DETAIL") Config {
        return sql.findOneById(DETAIL, id)
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        sql.deleteById(Config::class, id)
    }

    /**
     * 查询配置历史
     */
    @GetMapping("/{id}/histories")
    fun listHistories(@PathVariable id: Int): List<@FetchBy("HISTORY_LIST_ITEM") ConfigHistory> {
        return sql.executeQuery(ConfigHistory::class) {
            where(table.configId eq id)
            orderBy(table.id.desc())
            select(table.fetch(HISTORY_LIST_ITEM))
        }
    }

    companion object {
        val LIST_ITEM = newFetcher(Config::class).by {
            allScalarFields()
            yaml(false)
            creator {
                displayName()
            }
        }

        val HISTORY_LIST_ITEM = newFetcher(ConfigHistory::class).by {
            allScalarFields()
            creator {
                displayName()
            }
        }

        val DETAIL = newFetcher(Config::class).by {
            allScalarFields()
            creator {
                displayName()
            }
        }
    }
}