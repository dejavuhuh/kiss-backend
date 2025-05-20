package kiss.system.permission

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kiss.jimmer.BaseEntity
import kiss.jimmer.Operator
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne
import org.babyfish.jimmer.sql.Serialized

@Entity
interface PermissionAuditLog : BaseEntity, Operator {

    @ManyToOne
    val permission: Permission

    val operation: Operation

    @Serialized
    val operationDetails: OperationDetails?
}

enum class Operation {
    UPDATE,
    BIND_ROLES
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = UpdateDetails::class, name = "UPDATE"),
    JsonSubTypes.Type(value = BindRolesDetails::class, name = "BIND_ROLES"),
)
interface OperationDetails

class UpdateDetails(val diff: Permission) : OperationDetails
class BindRolesDetails(val roleIds: List<Int>) : OperationDetails
