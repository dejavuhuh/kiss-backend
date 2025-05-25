package kiss.system.api

import kiss.authentication.AuthenticationService
import org.babyfish.jimmer.client.runtime.Operation
import org.babyfish.jimmer.client.runtime.Service
import org.babyfish.jimmer.spring.client.Metadatas
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMethod

@Component
class ApiCollector(val sql: KSqlClient) : ApplicationRunner {

    val ignoredControllers = listOf(
        AuthenticationService::class
    )

    override fun run(args: ApplicationArguments) {
        val metadata = Metadatas.create(false, null, null)
        val apiGroups = metadata.services
            .filter { it.javaType.kotlin !in ignoredControllers }
            .map(::createApiGroup)
        sql.saveEntities(apiGroups)
    }

    private fun createApiGroup(service: Service): ApiGroup {
        val controllerClass = service.javaType
        return ApiGroup {
            name = service.doc?.value ?: controllerClass.name
            apis = service.operations.map(::createApi)
        }
    }

    private fun createApi(operation: Operation): Api {
        val httpMethod = operation.httpMethods[0]
        return Api {
            name = operation.doc?.value ?: operation.name
            method = RequestMethod.resolve(httpMethod.name)
                ?: throw IllegalStateException("Unsupported HTTP method: $httpMethod")
            path = operation.uri
        }
    }
}