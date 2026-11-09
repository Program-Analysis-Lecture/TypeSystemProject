package whilelang

/** Runtime values produced by evaluating WHILE expressions. */
sealed interface RuntimeValue {
    data class Integer(val value: Int) : RuntimeValue
    data class Boolean(val value: kotlin.Boolean) : RuntimeValue
}

/** The current values of variables that have been initialized. */
typealias Store = Map<String, RuntimeValue>

/** Result of interpreting an expression, statement, or program. */
sealed interface InterpretationResult<out T> {
    data class Success<T>(val value: T) : InterpretationResult<T>
    data class Failure(val message: String) : InterpretationResult<Nothing>
}

/**
 * Evaluate this expression in [store].
 *
 * Variables not present in the store have not been initialized. Students:
 * implement each expression case and report runtime failures as [InterpretationResult.Failure].
 */
fun Expr.evaluate(store: Store): InterpretationResult<RuntimeValue> =
    TODO("Implement expression evaluation")

/**
 * Execute this statement using the declarations in [environment] and current [store].
 *
 * Return the updated store on success. A declared variable may be assigned
 * before its first read; assignments to undeclared variables and runtime type
 * mismatches should fail. Labels do not affect execution.
 */
fun Statement.execute(
    environment: TypeEnvironment,
    store: Store,
): InterpretationResult<Store> =
    TODO("Implement statement execution")

/**
 * Interpret this program starting with all declared variables uninitialized.
 *
 * Students: reject duplicate declarations, build the declaration environment,
 * and whilelang.execute the body. The returned store contains initialized variables only.
 */
fun Program.interpret(): InterpretationResult<Store> =
    TODO("Implement program interpretation")
