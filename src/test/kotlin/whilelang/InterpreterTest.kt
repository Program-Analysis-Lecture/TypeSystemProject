package whilelang

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class InterpreterTest {
    private fun assertValue(expression: Expr, store: Store = emptyMap(), expected: RuntimeValue) {
        assertEquals(InterpretationResult.Success(expected), expression.evaluate(store))
    }

    private fun assertEvaluationFails(expression: Expr, store: Store = emptyMap()) {
        assertIs<InterpretationResult.Failure>(expression.evaluate(store))
    }

    private fun assertProgramResult(program: Program, expected: Store) {
        assertEquals(InterpretationResult.Success(expected), program.interpret())
    }

    private fun assertProgramFails(program: Program) {
        assertIs<InterpretationResult.Failure>(program.interpret())
    }

    @Test
    fun `literals and variables evaluate to runtime values`() {
        assertValue(IntLiteral(7), expected = RuntimeValue.Integer(7))
        assertValue(BoolLiteral(true), expected = RuntimeValue.Boolean(true))
        assertValue(
            Variable("n"),
            store = mapOf("n" to RuntimeValue.Integer(12)),
            expected = RuntimeValue.Integer(12),
        )
        assertEvaluationFails(Variable("missing"))
    }

    @Test
    fun `arithmetic evaluates all supported operators`() {
        assertValue(
            Arithmetic(ArithmeticOperator.ADD, IntLiteral(3), IntLiteral(4)),
            expected = RuntimeValue.Integer(7),
        )
        assertValue(
            Arithmetic(ArithmeticOperator.SUBTRACT, IntLiteral(3), IntLiteral(4)),
            expected = RuntimeValue.Integer(-1),
        )
        assertValue(
            Arithmetic(ArithmeticOperator.MULTIPLY, IntLiteral(-3), IntLiteral(4)),
            expected = RuntimeValue.Integer(-12),
        )
    }

    @Test
    fun `comparisons and boolean operators evaluate`() {
        assertValue(LessThan(IntLiteral(2), IntLiteral(3)), expected = RuntimeValue.Boolean(true))
        assertValue(Equal(IntLiteral(4), IntLiteral(4)), expected = RuntimeValue.Boolean(true))
        assertValue(Equal(BoolLiteral(false), BoolLiteral(true)), expected = RuntimeValue.Boolean(false))
        assertValue(Not(BoolLiteral(false)), expected = RuntimeValue.Boolean(true))
        assertValue(
            And(BoolLiteral(true), Not(BoolLiteral(false))),
            expected = RuntimeValue.Boolean(true),
        )
    }

    @Test
    fun `program executes assignments and sequences`() {
        assertProgramResult(
            Program(
                declarations = listOf(
                    VariableDeclaration("n", Type.INT),
                    VariableDeclaration("positive", Type.BOOL),
                ),
                body = Sequence(
                    Assign("n", IntLiteral(5), 1),
                    Assign("positive", LessThan(IntLiteral(0), Variable("n")), 2),
                ),
            ),
            mapOf(
                "n" to RuntimeValue.Integer(5),
                "positive" to RuntimeValue.Boolean(true),
            ),
        )
    }

    @Test
    fun `if executes only the selected branch`() {
        assertProgramResult(
            Program(
                declarations = listOf(VariableDeclaration("n", Type.INT)),
                body = If(
                    condition = BoolLiteral(false),
                    guardLabel = 1,
                    thenBranch = Assign("n", IntLiteral(1), 2),
                    elseBranch = Assign("n", IntLiteral(2), 3),
                ),
            ),
            mapOf("n" to RuntimeValue.Integer(2)),
        )
    }

    @Test
    fun `while repeatedly executes its body until its condition is false`() {
        assertProgramResult(
            Program(
                declarations = listOf(VariableDeclaration("n", Type.INT)),
                body = Sequence(
                    Assign("n", IntLiteral(0), 1),
                    While(
                        condition = LessThan(Variable("n"), IntLiteral(3)),
                        guardLabel = 2,
                        body = Assign(
                            "n",
                            Arithmetic(ArithmeticOperator.ADD, Variable("n"), IntLiteral(1)),
                            3,
                        ),
                    ),
                ),
            ),
            mapOf("n" to RuntimeValue.Integer(3)),
        )
    }

    @Test
    fun `declared variables remain uninitialized until assigned`() {
        assertProgramResult(
            Program(
                declarations = listOf(VariableDeclaration("unused", Type.INT)),
                body = Skip(1),
            ),
            emptyMap(),
        )
        assertProgramFails(
            Program(
                declarations = listOf(VariableDeclaration("n", Type.INT)),
                body = Assign("n", Variable("n"), 1),
            ),
        )
    }

    @Test
    fun `program rejects duplicate declarations and undeclared assignment targets`() {
        assertProgramFails(
            Program(
                declarations = listOf(
                    VariableDeclaration("n", Type.INT),
                    VariableDeclaration("n", Type.BOOL),
                ),
                body = Skip(1),
            ),
        )
        assertProgramFails(
            Program(
                declarations = emptyList(),
                body = Assign("missing", IntLiteral(1), 1),
            ),
        )
    }

    @Test
    fun `execution rejects assignment values with a type different from the declaration`() {
        assertProgramFails(
            Program(
                declarations = listOf(VariableDeclaration("flag", Type.BOOL)),
                body = Assign("flag", IntLiteral(1), 1),
            ),
        )
    }

    @Test
    fun `execution fails when evaluating a non-boolean guard`() {
        assertProgramFails(
            Program(
                declarations = listOf(VariableDeclaration("n", Type.INT)),
                body = While(IntLiteral(1), 1, Skip(2)),
            ),
        )
    }
}
