package kiss

import org.babyfish.jimmer.client.EnableImplicitApi
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableImplicitApi
@SpringBootApplication
class KissApplication

fun main(args: Array<String>) {
    runApplication<KissApplication>(*args)
}
