package kiss.system.api

import kiss.authentication.AuthenticationFilter
import org.babyfish.jimmer.client.runtime.Operation
import org.babyfish.jimmer.client.runtime.Service
import org.babyfish.jimmer.spring.client.Metadatas
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMethod

@Order(1)
@Component
class ApiCollector(val sql: KSqlClient, authenticationFilter: AuthenticationFilter) : ApplicationRunner {

    val ignoredApis = authenticationFilter.whiteList + listOf(
        "/current-user"
    )

    @Transactional
    override fun run(args: ApplicationArguments) {
        val metadata = Metadatas.create(false, null, null)
        val apiGroups = metadata.services.map(::createApiGroup)
        sql.saveEntities(apiGroups)
    }

    private fun createApiGroup(service: Service): ApiGroup {
        val controllerClass = service.javaType
        return ApiGroup {
            name = service.doc?.value ?: controllerClass.name
            apis = service.operations
                .filter { it.uri !in ignoredApis }
                .map(::createApi)
        }
    }

    private fun createApi(operation: Operation): Api {
        val httpMethod = operation.httpMethods[0]
        return Api {
            name = operation.doc?.value ?: operation.name
            method = RequestMethod.valueOf(httpMethod.name)
            path = operation.uri
        }
    }
}