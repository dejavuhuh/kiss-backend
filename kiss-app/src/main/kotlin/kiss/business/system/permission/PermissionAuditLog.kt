package kiss.business.system.permission

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import kiss.infrastructure.jimmer.BaseEntity
import kiss.infrastructure.jimmer.Operator
import kiss.business.system.permission.dto.PermissionInput
import org.babyfish.jimmer.sql.*

@Entity
interface PermissionAuditLog : BaseEntity, Operator {

    @ManyToOne
    @JoinColumn(foreignKeyType = ForeignKeyType.FAKE)
    val permission: Permission?

    val operation: Operation

    @Serialized
    val operationDetails: OperationDetails?
}

enum class Operation {
    CREATE,
    UPDATE,
    BIND_ROLES,
    DELETE
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = CreateDetails::class, name = "CREATE"),
    JsonSubTypes.Type(value = UpdateDetails::class, name = "UPDATE"),
    JsonSubTypes.Type(value = BindRolesDetails::class, name = "BIND_ROLES"),
)
interface OperationDetails

class CreateDetails(val input: PermissionInput) : OperationDetails
class UpdateDetails(val diff: Permission) : OperationDetails
class BindRolesDetails(val roleIds: List<Int>) : OperationDetails
