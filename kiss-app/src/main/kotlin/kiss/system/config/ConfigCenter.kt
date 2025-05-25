package kiss.system.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.readValue
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Component

@Component
class ConfigCenter(val sql: KSqlClient) {
    val objectMapper: ObjectMapper = ObjectMapper(YAMLFactory()).findAndRegisterModules()

}

inline fun <reified T> ConfigCenter.readYamlAsObject(name: String): T {
    val yaml = sql.createQuery(Config::class) {
        where(table.name eq name)
        select(table.yaml)
    }.fetchOne()

    if (yaml == null) {
        throw IllegalStateException("Yaml not configured: $name")
    }

    return objectMapper.readValue<T>(yaml)
}