# Lab 2 Notes

## 2.1
After the tests of ai, the coverage presented was 

![img.png](img.png)

I then added more tests to get the coverage to 100%, excluding the Stock.java file, since its methods were very simple and didn't require any testing.

![img_1.png](img_1.png)

# f) Maven Command Breakdown

### 1. `mvn test`

- **Purpose**: Runs the unit tests .
- **Explanation**:
    - The `mvn test` command compiles the project and runs the unit tests that are typically located in the `src/test/java` directory.
---

### 2. `mvn package`

- **Purpose**: Compiles and packages the code into a deployable artifact.
- **Explanation**:
    - The `mvn package` command compiles the project and then packages it according to the project's packaging configuration in `pom.xml`.
    - It runs the `compile` phase followed by the `package` phase.
    - This command **does not run tests by default** but will compile the source and resources.

---

### 3. `mvn package -DskipTests=true`

- **Purpose**: Packages the project without running any tests.
- **Explanation**:
    - This command is similar to `mvn package`, but it skips the test phase.
    - The `-DskipTests=true` option tells Maven to skip the execution of unit tests, but **it still compiles the tests**.
    - The package will be generated, but no tests will be executed during this phase.

---

### 4. `mvn failsafe:integration-test`

- **Purpose**: Executes the integration tests in the project.
- **Explanation**:
    - The `mvn failsafe:integration-test` command runs the integration tests using the **Failsafe plugin**.
    - This plugin is used specifically for integration tests, which are tests that require external systems, like databases or APIs, and might take longer to run.
    - It is typically used in conjunction with the `mvn verify` command, which handles both the unit and integration tests.
    - This phase will run tests in the `src/test/java` directory or the `src/it/java` directory (for integration tests) based on the configuration in the `pom.xml`.

---

### 5. `mvn install`

- **Purpose**: Compiles, tests, and packages the code, then installs the artifact into the Maven repository.
- **Explanation**:
    - The `mvn install` runs the entire build lifecycle.
    - It includes `compile`, `test`, `package`, and `install` phases.

---

### Summary

- `mvn test`: Runs only unit tests.
- `mvn package`: Compiles and packages the project into an artifact without running tests.
- `mvn package -DskipTests=true`: Packages the project without running any tests.
- `mvn failsafe:integration-test`: Runs integration tests using the Failsafe plugin (typically used in the `integration-test` phase).
- `mvn install`: Runs the full build lifecycle (compile, test, package, install) and installs the artifact into the local Maven repository.

