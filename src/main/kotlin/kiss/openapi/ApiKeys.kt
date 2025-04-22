package kiss.openapi

import kiss.jimmer.BaseEntity

//@Entity
interface ApiKeys : BaseEntity {

    val accessKey: String

    val secretKey: String
}