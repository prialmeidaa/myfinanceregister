# Code standards

This document complements `AGENTS.md` and defines the conventions that must be followed when evolving the application.

## General rules

- Do not insert comments into the code, except when absolutely necessary, such as in a complex regular expression or in a not-so-obvious rule.
- Never create classes or `.kt` files with more than 100 lines.
- Methods and functions must have a maximum of 30 lines. If the behavior is larger, split it into more methods.
- Do not nest more than 3 conditional structures per method (`if`, `else`, `when`, `for`, `while`, etc.).
- Prefer the use of guard clauses (`return`, `continue`, `break`, `throw`) to reduce the accumulation of conditional logic.
- Avoid more than 3 parameters in methods and functions. When necessary, use transport objects (DTOs) to encapsulate the parameters.
- Extract magic numbers and strings into constants.
- Declare variables close to where they are used.
- Always use the `camelCase` pattern for method and variable names.

## 1) Do not insert comments

The code should be self-explanatory. Comments should only appear in extremely rare cases, such as explaining a complex regular expression or a rule that is not intuitive.

Acceptable example:

```kotlin
val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
// Regex to validate common email addresses.
```

Not recommended example:

```kotlin
// Adds up the values in the list
val total = values.sum()
```

## 2) Classes and files with a maximum of 100 lines

Each class or `.kt` file should remain small and focused. If a class grows too much, it is probably taking on more than one responsibility.

Correct example:

```kotlin
class InterestCalculator {
    fun calculate(value: Double, rate: Double, months: Int): Double {
        return value * Math.pow(1 + rate, months.toDouble())
    }
}
```

Not recommended example:

```kotlin
class CompleteFinancialProcess {
    // 120 lines of mixed logic: validation, calculation, persistence, sending, UI
}
```

## 3) Methods and functions with a maximum of 30 lines

Functions should be small and have a clear responsibility. When the body starts to grow, extract parts into other methods.

Correct example:

```kotlin
fun calculateDiscount(value: Double, percentage: Double): Double {
    if (value <= 0) return 0.0
    if (percentage <= 0) return 0.0

    return value * (percentage / 100)
}
```

Not recommended example:

```kotlin
fun processOrder(order: Order) {
    if (order.active) {
        if (order.paid) {
            if (order.stockAvailable) {
                // 35 lines of logic
            }
        }
    }
}
```

## 4) Do not nest more than 3 conditional structures

Avoid deep chains of conditionals. When there are more than three checks, restructure the logic or extract methods.

Correct example:

```kotlin
fun canAccessUser(user: User, currentProfile: Profile): Boolean {
    if (user.blocked) return false
    if (currentProfile.required && !user.hasPermission) return false
    return user.active
}
```

Not recommended example:

```kotlin
fun canAccessUser(user: User, currentProfile: Profile): Boolean {
    if (user.active) {
        if (!user.blocked) {
            if (currentProfile.required) {
                return user.hasPermission
            }
        }
    }
    return false
}
```

## 5) Prefer guard clauses

Use early `return` statements to avoid nested conditional logic and reduce branch buildup.

Correct example:

```kotlin
fun validateAccount(account: Account): Boolean {
    if (account.number.isBlank()) return false
    if (account.balance < 0) return false
    if (!account.active) return false

    return true
}
```

Not recommended example:

```kotlin
fun validateAccount(account: Account): Boolean {
    var valid = true

    if (account.number.isNotBlank()) {
        if (account.balance >= 0) {
            if (account.active) {
                valid = true
            } else {
                valid = false
            }
        } else {
            valid = false
        }
    } else {
        valid = false
    }

    return valid
}
```

## 6) Avoid more than 3 parameters in methods and functions

When a function requires many arguments, use an object to carry the data and make the call more readable.

Correct example:

```kotlin
data class TransactionFilter(
    val startDate: String,
    val endDate: String,
    val category: String,
    val status: String
)

fun listTransactions(filter: TransactionFilter): List<Transaction> {
    return emptyList()
}
```

Call:

```kotlin
val filter = TransactionFilter(
    startDate = "2026-01-01",
    endDate = "2026-12-31",
    category = "food",
    status = "paid"
)

listTransactions(filter)
```

Not recommended example:

```kotlin
fun listTransactions(
    startDate: String,
    endDate: String,
    category: String,
    status: String,
    userId: Long
): List<Transaction> {
    return emptyList()
}
```

## 7) Extract magic numbers and strings into constants

Fixed and repeated values should be centralized in constants with clear names.

Correct example:

```kotlin
private const val DEFAULT_RATE = 0.10
private const val ERROR_MESSAGE = "Invalid value"

fun calculateFinalValue(value: Double): Double {
    return value + (value * DEFAULT_RATE)
}
```

Not recommended example:

```kotlin
fun calculateFinalValue(value: Double): Double {
    return value + (value * 0.10)
}
```

## 8) Declare variables close to where they are used

Keep the variable scope small and close to the point where it is actually needed.

Correct example:

```kotlin
fun applyDiscount(value: Double): Double {
    val discount = 0.15
    val discountedValue = value * (1 - discount)
    return discountedValue
}
```

Not recommended example:

```kotlin
fun applyDiscount(value: Double): Double {
    val discount: Double
    val discountedValue: Double

    discount = 0.15
    discountedValue = value * (1 - discount)
    return discountedValue
}
```

## 9) Always use camelCase for methods and variables

All method, parameter, and variable names must follow the `camelCase` pattern.

Correct example:

```kotlin
val totalValue = 200.0

fun calculateInterest(value: Double): Double {
    return value * 0.02
}
```

Not recommended example:

```kotlin
val TotalValue = 200.0

fun CalculateInterest(value: Double): Double {
    return value * 0.02
}
```

## Practical summary

- Short, readable, and focused code.
- Small functions with a single responsibility.
- Each method should be easy to understand in a quick read.
- Avoid excessive branching and nested validations.
- Use constants for fixed values.
- Use DTOs for complex parameters.
- Keep names consistent and in `camelCase`.
- Do not comment code unless there is a real need.

## Validation flow

- Use the project's Gradle Wrapper (`./gradlew` or `./gradlew.bat`) for builds and tests instead of global binaries.
- Before finalizing a relevant change, run the smallest validation set that covers the change.
