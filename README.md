# Type System Exercises

Complete every `TODO` in the starter code. Run all tests with `./gradlew test`
(`gradlew.bat test` on Windows); a test fails until its `TODO` is implemented.

## 1. WHILE type checker (`whilelang/TypeChecker.kt`)

Implement `Expr.typeOf` and `Statement.typeOf`. The AST is in `WhileLanguage.kt`.

- Literals are `int` or `bool`; a variable has its declared type in the environment, and an undeclared one is an error.
- `+ - * <` take `int` operands; `+ - *` give `int`, `<` gives `bool`.
- `=` needs operands of the same type and gives `bool`; `not` and `and` take and give `bool`.
- Assignment: the target must be declared and the value must match its type.
- `if` and `while` conditions must be `bool`; sequences and branches must all be well-typed.
- A well-typed statement returns `Success(StatementType.OK)`; anything else is a `Failure`. Labels do not affect typing.

Tests: `whilelang/TypeCheckerTest.kt`.

## 2. WHILE interpreter (`whilelang/Interpreter.kt`)

Implement `Expr.evaluate`, `Statement.execute` and `Program.interpret`.

- Declared variables start uninitialized; the final store holds only assigned ones.
- Return a `Failure` (do not crash) for reading an uninitialized variable, assigning to an undeclared one, a value of the wrong type, a non-`bool` guard, or duplicate declarations.
- Labels do not affect execution.

Tests: `whilelang/InterpreterTest.kt`.

## 3. Locals and casts with SootUp (`soot/MethodInspector.kt`)

Implement `listLocals` (name and type of each local, sorted by name) and `listCasts` (every cast in a method's Jimple body). Hints are in the KDoc.

- Run `./gradlew runSoot` to print the Jimple, locals and casts of `CastExample`.
- Inspect your own classes: `./gradlew runSoot --args="<classpath-dir> <class-name> [method-name]"`.

Tests: `soot/MethodInspectorTest.kt`.
