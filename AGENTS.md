# Project guide

These instructions describe the structure and commands of this repository. Consult them at the start of each session and keep them updated when the structure or development flow changes.

## Code standards

Consult the document in `.github/agents/code-standards.md` for style, architecture, testing, and code quality conventions that must be followed throughout the project.

Also consult the [test.md](.github/agents/test.md) document.

## Folder and file structure

- `src/main/kotlin/`: Kotlin application code.
  - `paa/myfinanceregister/MyFinanceRegisterApplication.kt`: Spring Boot entry point and main configuration.
  - `paa/myfinanceregister/MainView.kt`: existing Vaadin screen configured on the root route (`/`).
  - Organize application code under `paa/myfinanceregister/` by responsibility:
    - `domain/`: domain models and repository definitions.
    - `services/`: business logic and application services.
    - `ui/`: Vaadin views and other user-interface code.
  - Keep Vaadin-specific code in `ui/` and business logic in `services/`; do not place either in `domain/`.
- `src/main/resources/`: application resources and configuration. `application.yml` configures the in-memory H2 datasource and JPA.
- `src/main/frontend/`: Vaadin frontend resources. `generated/` contains automatically generated files; do not edit these files manually.
- `src/test/kotlin/`: automated tests in Kotlin/JUnit. The existing test verifies Spring context loading.
- `build.gradle.kts`: plugins, dependencies, versions, and Gradle task configuration. Current dependencies include Kotlin, Spring Boot, Vaadin, and H2.
- `settings.gradle.kts`: project name and Gradle configuration.
- `gradle.properties`: Gradle properties and Kotlin style settings.
- `gradle/wrapper/`: Gradle Wrapper configuration and files; `gradle-wrapper.properties` pins the Gradle version.
- `gradlew` and `gradlew.bat`: Wrapper scripts for Linux/macOS and Windows, respectively. Prefer them over a global Gradle installation.
- `build/`, `target/`, `.gradle/`, and `.kotlin/`: generated outputs/cache; they are not source code and should not be edited manually.
- `.idea/`: local IntelliJ IDEA settings.

## Requirements and dependencies

- JDK 21, required by the Kotlin toolchain configured in `build.gradle.kts`.
- There is no separate dependency installation step or maintained Node manager in this repository. The Gradle Wrapper downloads the configured Gradle distribution and resolves the dependencies declared in `build.gradle.kts` automatically when running tasks.
- Run commands from the repository root. On Windows, use `./gradlew.bat`; on Linux/macOS, use `./gradlew`.

## Run the application

```powershell
.\gradlew.bat bootRun
```

On Linux/macOS:

```sh
./gradlew bootRun
```

Open `http://localhost:8080`. The application uses in-memory H2 as configured in `src/main/resources/application.yml`; data does not persist across restarts.

## Tests

Run all tests:

```powershell
.\gradlew.bat test
```

On Linux/macOS, use `./gradlew test`. Tests are in `src/test/kotlin/` and use JUnit Platform.

## Build

To compile, run tests, and produce build artifacts:

```powershell
.\gradlew.bat build
```

On Linux/macOS, use `./gradlew build`. Artifacts are written to `build/`; do not version or edit generated files in that folder.