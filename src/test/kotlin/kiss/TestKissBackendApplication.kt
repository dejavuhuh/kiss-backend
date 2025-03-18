package kiss

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<KissBackendApplication>().with(TestcontainersConfiguration::class).run(*args)
}
