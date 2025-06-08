package kiss.export.demo

import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/export/demo/big-data")
class BigDataService(
    val sql: KSqlClient,
    val jdbcTemplate: JdbcTemplate,
) {

    @PostMapping("/generate")
    fun generate(@RequestParam count: Long) {
        val value = Long.MAX_VALUE.toString()

        val data = (1..count).map {
            BigData {
                a = value
                b = value
                c = value
                d = value
                e = value
                f = value
                g = value
                h = value
                i = value
                j = value
            }
        }
        
        jdbcTemplate.batchUpdate(
            "INSERT INTO big_data (a, b, c, d, e, f, g, h, i, j) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            data,
            10000
        ) { ps, it ->
            ps.setString(1, it.a)
            ps.setString(2, it.b)
            ps.setString(3, it.c)
            ps.setString(4, it.d)
            ps.setString(5, it.e)
            ps.setString(6, it.f)
            ps.setString(7, it.g)
            ps.setString(8, it.h)
            ps.setString(9, it.i)
            ps.setString(10, it.j)
        }
    }
}
