package whilelang

/**
 * The types assigned to variables by a program's declarations.
 *
 * A missing name means the variable is not declared and should be rejected by
 * the type checker.
 */
typealias TypeEnvironment = Map<String, Type>

/** Result of checking an expression or statement. */
sealed interface TypeCheckResult<out T> {
    data class Success<T>(val type: T) : TypeCheckResult<T>
    data class Failure(val message: String) : TypeCheckResult<Nothing>
}

/** The only type a well-typed statement receives. */
enum class StatementType {
    OK,
}

/**
 * Return the type of this expression, or a failure if it is not well-typed.
 *
 * Students: implement this function. Use [environment] to look up variables,
 * and check that each operator is applied to operands of the required types.
 */
fun Expr.typeOf(environment: TypeEnvironment): TypeCheckResult<Type> =
    TODO("Implement expression type checking")

/**
 * Check this statement and return [StatementType.OK] if it is well-typed.
 *
 * Students: implement this function. Check assigned variables and expressions,
 * Boolean conditions, and every statement in sequences and branches. Return a
 * failure when any part of the statement is ill-typed.
 */
fun Statement.typeOf(environment: TypeEnvironment): TypeCheckResult<StatementType> =
    TODO("Implement statement type checking")
