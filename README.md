# WHILE Type-Checker Exercise

Implement a type checker and interpreter for the small WHILE language in
`src/main/kotlin`. The syntax tree is defined in `WhileLanguage.kt`; the
type-checker and interpreter exercises are in `TypeChecker.kt` and
`Interpreter.kt`.

## Getting started

1. Open the project in IntelliJ IDEA, or use a terminal with a compatible JDK.
2. Read `WhileLanguage.kt` to see the expression and statement forms.
3. Implement `Expr.typeOf` and `Statement.typeOf` in `TypeChecker.kt`.
4. Implement the interpreter functions in `Interpreter.kt`.
5. Run the tests with `./gradlew test` (on Windows, use `gradlew.bat test`).

The starter functions contain `TODO`s, so tests for an exercise are expected
to fail until its implementation is complete. `./gradlew testClasses` compiles
the tests without running them.

## Language overview

- Types: `int` and `bool`.
- Expressions: integer and Boolean literals, variables, integer arithmetic
  (`+`, `-`, `*`), integer comparison (`<`), equality (`=`), Boolean negation
  (`not`), and conjunction (`and`).
- Statements: labeled assignment and `skip`, sequencing, labeled `if`, and
  labeled `while`.
- Programs declare their variables and types before the statement.

The Kotlin classes for these forms are in `WhileLanguage.kt`. For example,
`Arithmetic(ArithmeticOperator.ADD, left, right)` represents `left + right`;
`VariableDeclaration("count", Type.INT)` declares an integer variable.

## Type-checker interface

`TypeEnvironment` maps variable names to their declared types. Use the
environment to resolve every `Variable`; an undeclared name is an error.
Expression checking returns `TypeCheckResult.Success(Type.INT)` or
`TypeCheckResult.Success(Type.BOOL)` for valid expressions, and
`TypeCheckResult.Failure` for invalid ones.

Statement checking returns `TypeCheckResult.Success(StatementType.OK)` when
the whole statement is well-typed, or `TypeCheckResult.Failure` otherwise.
Check all subexpressions and nested statements, not only the outermost node.
Statement labels identify source locations but do not affect typing.

The tests in `src/test/kotlin/TypeCheckerTest.kt` cover valid and invalid
expressions and statements, including undeclared variables, mismatched operand
types, nested expressions, and control-flow conditions.

