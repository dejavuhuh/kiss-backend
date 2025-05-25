package kiss

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<KissApplication>().with(TestcontainersConfiguration::class).run(*args)
}
