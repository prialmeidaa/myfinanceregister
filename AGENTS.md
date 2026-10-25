# Project guide

These instructions describe the structure and commands of this repository. Consult them at the start of each session.

## Code standards

Consult the document in `.github/agents/code-standards.md` for style, architecture, testing, and code quality conventions that must be followed throughout the project.

Also consult the [test.md](.github/agents/test.md) document.

Every implementation must be accompanied by automated tests that cover the implemented behavior.

## Folder and file structure

- `src/main/kotlin/paa/myfinanceregister/`: Kotlin application code, organized by responsibility:
  - `domain/model/`: domain entities and models.
  - `domain/repository/`: persistence repositories.
  - `services/`: business logic and application services.
  - `ui/`: Vaadin views, reusable components, and layouts.
  - `security/`: Spring Security and Vaadin security configuration.
- `src/main/resources/`: application configuration and resources, including the Spring profiles and datasource settings.
- `src/main/frontend/`: Vaadin frontend resources. Do not edit files under `generated/` manually.
- `src/test/`: automated tests. Keep the test package and directory structure equivalent to the source file being tested.
- `gradle/`, `gradlew`, and `gradlew.bat`: Gradle Wrapper configuration and scripts.
- Root Gradle files (`build.gradle.kts`, `settings.gradle.kts`, and `gradle.properties`): build, dependency, project, and tool configuration.
- `build/`, `target/`, `.gradle/`, and `.kotlin/`: generated outputs and caches; do not edit them manually.

## Requirements and dependencies

- JDK 21, required by the Kotlin toolchain configured in `build.gradle.kts`.
- `vaadin-dev` is available only for development and test runtime, not packaged in the production artifact.
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