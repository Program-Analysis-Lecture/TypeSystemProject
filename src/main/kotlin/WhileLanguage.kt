/**
 * Abstract syntax for the WHILE language used in the type-checker exercise.
 *
 */
enum class Type {
    INT,
    BOOL,
}

/** Source labels annotate assignments, skip statements, and control guards. */
typealias Label = Int

sealed interface Expr

data class IntLiteral(val value: Int) : Expr

data class BoolLiteral(val value: Boolean) : Expr

data class Variable(val name: String) : Expr

enum class ArithmeticOperator {
    ADD,
    SUBTRACT,
    MULTIPLY,
}

/** Represents `left + right`, `left - right`, or `left * right`. */
data class Arithmetic(
    val operator: ArithmeticOperator,
    val left: Expr,
    val right: Expr,
) : Expr

data class LessThan(val left: Expr, val right: Expr) : Expr

data class Equal(val left: Expr, val right: Expr) : Expr

data class Not(val expression: Expr) : Expr

data class And(val left: Expr, val right: Expr) : Expr

sealed interface Statement

data class Assign(
    val variable: String,
    val expression: Expr,
    val label: Label,
) : Statement

data class Skip(val label: Label) : Statement

data class Sequence(val first: Statement, val second: Statement) : Statement

data class If(
    val condition: Expr,
    val guardLabel: Label,
    val thenBranch: Statement,
    val elseBranch: Statement,
) : Statement

data class While(
    val condition: Expr,
    val guardLabel: Label,
    val body: Statement,
) : Statement

data class VariableDeclaration(
    val name: String,
    val type: Type,
)

data class Program(
    val declarations: List<VariableDeclaration>,
    val body: Statement,
)
