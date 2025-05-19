package kiss.system.permission

import com.fasterxml.jackson.annotation.JsonTypeInfo
import kiss.jimmer.BaseEntity
import kiss.jimmer.Operator
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.ManyToOne

@Entity
interface PermissionAuditLog : BaseEntity, Operator {

    @ManyToOne
    val permission: Permission

    val operation: Operation

    val operationDetails: OperationDetails?
}

enum class Operation {
    UPDATE,
    BIND_ROLES
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
interface OperationDetails

class BindRolesDetails(val roleIds: List<Int>) : OperationDetails
class UpdateDetails(val diff: Permission) : OperationDetails
