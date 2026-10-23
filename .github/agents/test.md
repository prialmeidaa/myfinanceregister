# Test rules

- All code must be covered by automated tests. The minimum required coverage is 80%. In addition to the percentage, prioritize coverage of the most critical business behaviors: for example, inserting a financial record is more critical than validating the content of a field.
- Follow the **FIRST** principles:
  - **Fast:** prioritize tests that run quickly. Use patterns such as *stub* to replace external or slow dependencies.
  - **Independent:** each test must be runnable in isolation, without depending on other tests or execution order.
  - **Repeatable:** the same tests must produce the same results on every run. Use patterns such as *mock* to control variable sources, such as dates and external API responses.
  - **Self-validating:** each test must explicitly verify the expected results and behaviors with good *assertions*/*expects*. Line coverage alone does not prove that the behavior is correct.
  - **Timely:** write the tests before or alongside the implementation, following **Test-First**. Organize test creation in the order that makes the most sense to get efficient feedback.
- Structure each test as **Given/When/Then**: prepare the context, execute the action, and verify the result.
- Create a separate test file and test class for each Kotlin source file being tested. Keep each test class focused on a single production Kotlin file instead of combining tests for multiple files in one class.
- Follow the test pyramid: keep a broad base of unit tests, complemented by integration tests and a smaller number of end-to-end (E2E) tests.
- Each test method should focus on a single concept or behavior. Do not mix checks for different behaviors in the same test.
