import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TypeCheckerTest {
    /** A simple environment with some declared variables. */
    private val environment = mapOf(
        "i" to Type.INT,
        "j" to Type.INT,
        "b" to Type.BOOL,
        "c" to Type.BOOL,
    )

    private fun assertType(expression: Expr, expected: Type) {
        assertEquals(TypeCheckResult.Success(expected), expression.typeOf(environment))
    }

    private fun assertExpressionRejected(expression: Expr) {
        assertIs<TypeCheckResult.Failure>(expression.typeOf(environment))
    }

    private fun assertStatementAccepted(statement: Statement) {
        assertEquals(
            TypeCheckResult.Success(StatementType.OK),
            statement.typeOf(environment),
        )
    }

    private fun assertStatementRejected(statement: Statement) {
        assertIs<TypeCheckResult.Failure>(statement.typeOf(environment))
    }

    @Test
    fun `literals and declared variables have their declared types`() {
        assertType(IntLiteral(42), Type.INT)
        assertType(BoolLiteral(true), Type.BOOL)
        assertType(Variable("i"), Type.INT)
        assertType(Variable("b"), Type.BOOL)
    }

    @Test
    fun `arithmetic operators accept integers and return integers`() {
        for (operator in ArithmeticOperator.entries) {
            assertType(
                Arithmetic(operator, Variable("i"), IntLiteral(2)),
                Type.INT,
            )
        }
    }

    @Test
    fun `comparisons and boolean operators return booleans`() {
        assertType(LessThan(Variable("i"), Variable("j")), Type.BOOL)
        assertType(Equal(Variable("i"), IntLiteral(0)), Type.BOOL)
        assertType(Equal(Variable("b"), BoolLiteral(false)), Type.BOOL)
        assertType(Not(Variable("b")), Type.BOOL)
        assertType(And(Variable("b"), BoolLiteral(true)), Type.BOOL)
    }

    @Test
    fun `expression type checking rejects undeclared variables`() {
        assertExpressionRejected(Variable("missing"))
    }

    @Test
    fun `expression type checking rejects operands of the wrong type`() {
        for (operator in ArithmeticOperator.entries) {
            assertExpressionRejected(Arithmetic(operator, Variable("i"), Variable("b")))
            assertExpressionRejected(Arithmetic(operator, Variable("b"), Variable("i")))
        }
        assertExpressionRejected(LessThan(Variable("i"), Variable("b")))
        assertExpressionRejected(LessThan(Variable("b"), Variable("i")))
        assertExpressionRejected(Equal(Variable("i"), Variable("b")))
        assertExpressionRejected(Not(Variable("i")))
        assertExpressionRejected(And(Variable("b"), Variable("i")))
        assertExpressionRejected(And(Variable("i"), Variable("b")))
    }

    @Test
    fun `nested expressions are checked recursively`() {
        val validArithmetic = Arithmetic(
            ArithmeticOperator.MULTIPLY,
            Arithmetic(ArithmeticOperator.ADD, Variable("i"), IntLiteral(1)),
            Arithmetic(ArithmeticOperator.SUBTRACT, Variable("j"), IntLiteral(2)),
        )
        assertType(LessThan(validArithmetic, IntLiteral(10)), Type.BOOL)

        assertExpressionRejected(
            Arithmetic(
                ArithmeticOperator.ADD,
                IntLiteral(1),
                Not(Variable("b")),
            ),
        )
        assertExpressionRejected(
            And(
                BoolLiteral(true),
                LessThan(Variable("missing"), IntLiteral(0)),
            ),
        )
        assertExpressionRejected(
            Not(And(Variable("b"), Variable("i"))),
        )
    }

    @Test
    fun `literals do not require a nonempty environment`() {
        assertEquals(TypeCheckResult.Success(Type.INT), IntLiteral(0).typeOf(emptyMap()))
        assertEquals(TypeCheckResult.Success(Type.BOOL), BoolLiteral(false).typeOf(emptyMap()))
        assertIs<TypeCheckResult.Failure>(Variable("i").typeOf(emptyMap()))
    }

    @Test
    fun `skip and well-typed assignments are accepted`() {
        assertStatementAccepted(Skip(label = 1))
        assertStatementAccepted(Assign("i", Arithmetic(ArithmeticOperator.ADD, Variable("i"), IntLiteral(1)), 2))
        assertStatementAccepted(Assign("b", And(Variable("b"), BoolLiteral(false)), 3))
    }

    @Test
    fun `assignment rejects undeclared targets and mismatched values`() {
        assertStatementRejected(Assign("missing", IntLiteral(1), 1))
        assertStatementRejected(Assign("i", BoolLiteral(true), 2))
        assertStatementRejected(Assign("b", IntLiteral(1), 3))
        assertStatementRejected(Assign("i", Variable("missing"), 4))
    }

    @Test
    fun `sequences check both statements`() {
        assertStatementAccepted(
            Sequence(
                Assign("i", IntLiteral(1), 1),
                Assign("b", LessThan(Variable("i"), IntLiteral(2)), 2),
            ),
        )
        assertStatementRejected(
            Sequence(
                Skip(1),
                Assign("i", BoolLiteral(false), 2),
            ),
        )
        assertStatementRejected(
            Sequence(
                Assign("i", BoolLiteral(false), 1),
                Skip(2),
            ),
        )
    }

    @Test
    fun `if and while require boolean conditions and well-typed bodies`() {
        assertStatementAccepted(
            If(
                condition = Variable("b"),
                guardLabel = 1,
                thenBranch = Assign("i", IntLiteral(1), 2),
                elseBranch = Skip(3),
            ),
        )
        assertStatementAccepted(
            While(
                condition = LessThan(Variable("i"), IntLiteral(10)),
                guardLabel = 4,
                body = Assign(
                    "i",
                    Arithmetic(ArithmeticOperator.ADD, Variable("i"), IntLiteral(1)),
                    5,
                ),
            ),
        )
        assertStatementRejected(If(Variable("i"), 1, Skip(2), Skip(3)))
        assertStatementRejected(While(IntLiteral(1), 1, Skip(2)))
        assertStatementRejected(
            If(
                condition = Variable("b"),
                guardLabel = 1,
                thenBranch = Assign("i", BoolLiteral(true), 2),
                elseBranch = Skip(3),
            ),
        )
        assertStatementRejected(
            If(
                condition = Variable("b"),
                guardLabel = 1,
                thenBranch = Skip(2),
                elseBranch = Assign("b", IntLiteral(0), 3),
            ),
        )
        assertStatementRejected(
            While(
                condition = Variable("b"),
                guardLabel = 4,
                body = Assign("missing", IntLiteral(1), 5),
            ),
        )
    }

    @Test
    fun `statement labels do not change typing`() {
        assertStatementAccepted(
            Sequence(
                Assign("i", IntLiteral(1), label = 7),
                Sequence(
                    Skip(label = 7),
                    While(BoolLiteral(false), guardLabel = 7, body = Skip(label = 7)),
                ),
            ),
        )
    }
}
