package kiss.system.config

import kiss.jimmer.insertOnly
import kiss.jimmer.updateOnly
import kiss.system.config.dto.ConfigInput
import org.babyfish.jimmer.client.FetchBy
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@Transactional
@RestController
@RequestMapping("/config")
class ConfigService(val sql: KSqlClient) {

    @GetMapping
    fun list(): List<@FetchBy("LIST_ITEM") Config> = sql.executeQuery(Config::class) {
        select(table.fetch(LIST_ITEM))
    }

    @PostMapping
    fun create(@RequestBody input: ConfigInput) {
        sql.insertOnly(input)
    }

    @PutMapping("/{id}/save-yaml")
    fun saveYaml(@PathVariable id: Int, @RequestBody yaml: String?) {
        sql.updateOnly(Config {
            this.id = id
            this.yaml = yaml
        })
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int): @FetchBy("DETAIL") Config {
        return sql.findOneById(DETAIL, id)
    }

    companion object {
        val LIST_ITEM = newFetcher(Config::class).by {
            allScalarFields()
            yaml(false)
            creator {
                username()
            }
        }

        val DETAIL = newFetcher(Config::class).by {
            allScalarFields()
            creator {
                username()
            }
        }
    }
}